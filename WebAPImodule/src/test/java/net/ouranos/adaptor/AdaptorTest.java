package net.ouranos.adaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.ouranos.common.context.ApiContext;

/**
 * Adaptorのテストクラス
 */
@RestClientTest(Adaptor.class)
public class AdaptorTest {

    /**
     * テスト対象クラス
     */
    @InjectMocks
    private Adaptor<Object> adaptor;

    /**
     * RestClient.Builderのモック
     */
    @Mock
    private RestClient.Builder mockRestClient;

    /**
     * RestClient.Builderのインスタンス
     */
    @Autowired
    private RestClient.Builder restClient;

    /**
     * MockRestServiceServerのインスタンス
     */
    private MockRestServiceServer mockServer;

    /**
     * テスト前処理
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(adaptor, "apiKey", "testapiKey");
        ReflectionTestUtils.setField(adaptor, "baseUrl", "http://localhost:8080/");
        mockServer = MockRestServiceServer.bindTo(restClient).build();
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

        // Excluded headers setup
        ReflectionTestUtils.setField(adaptor, "excludedHeaders", List.of("X-Forwarded-For", "X-amzn-"));

        when(mockRestClient.build()).thenReturn(restClient.build());
    }
    
    /**
     * sendGetメソッドのテスト response headerにX-tracking, linkが含まれる場合
     */
    @Test
    public void testSendGet() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String query = "query";
        String path = "/targetDataModel";
        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.GET))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON).header("X-Tracking", "test").header("link", "link"));
        // Act
        ResponseEntity<Object> response = adaptor.sendGET(new ApiContext(path, null, uuid, query, "Bearer token"));
        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        assertEquals(response.getHeaders().get("X-Tracking").get(0), "test");
        assertEquals(response.getHeaders().get("Link").get(0), "link"); 
        mockServer.verify();
    }

    /**
     * sendGetメソッドのテスト response headerにX-tracking, linkが含まれない場合
     */
    @Test
    public void testSendGet_withQuery_noHeader() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String query = "query";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.GET))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // Act
        ResponseEntity<Object> response = adaptor.sendGET(new ApiContext(path, null, uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        assertEquals(response.getHeaders().containsKey("Link"), false);
        mockServer.verify();
    }

    /**
     * sendGetメソッドのテスト クエリに[]が含まれている場合
     */
    @Test
    public void testSendGet_withQuery_brackets() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String query = "query[]";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.GET))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // Act
        ResponseEntity<Object> response = adaptor.sendGET(new ApiContext(path, null, uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        mockServer.verify();
    }

    /**
     * sendGetメソッドのテスト(queryがない場合)
     */
    @Test
    public void testSendGet_noQuery() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel"))
                  .andExpect(method(HttpMethod.GET))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON).header("X-Tracking", uuid.toString()));

        // Act
        ResponseEntity<Object> response = adaptor.sendGET(new ApiContext(path, null, uuid, null, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        mockServer.verify();
    }


    /**
     * sendPutメソッドのテスト response headerにX-tracking, linkが含まれる場合
     */
    @Test
    public void testSendPut_withHeader() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String query = "query";
        String path = "/targetDataModel";
        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.PUT))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON).header("X-Tracking", "test").header("link", "link"));

        // Act
        ResponseEntity<Object> response = adaptor.sendPUT(new ApiContext(path, "targetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        assertEquals(response.getHeaders().get("X-Tracking").get(0), "test");
        assertEquals(response.getHeaders().get("Link").get(0), "link"); 
        mockServer.verify();
    }

    /**
     * sendPutメソッドのテスト response headerにX-tracking, linkが含まれない場合
     */
    @Test
    public void testSendPut_noHeader() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String query = "query";
        String path = "/targetDataModel";
        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.PUT))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // Act
        ResponseEntity<Object> response = adaptor.sendPUT(new ApiContext(path, "targetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString()); 
        assertEquals(response.getHeaders().containsKey("Link"), false);
        mockServer.verify();
    }

    /**
     * sendPutメソッドのテスト クエリに[]が含まれている場合
     */
    @Test
    public void testSendPut_withQuery_brackets() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String query = "query[]";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.PUT))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // Act
        ResponseEntity<Object> response = adaptor.sendPUT(new ApiContext(path, "targetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        mockServer.verify();
    }

    /**
     * sendPutメソッドのテスト クエリがない場合
     */
    @Test
    public void testSendPut_noQuery() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel"))
                  .andExpect(method(HttpMethod.PUT))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // Act
        ResponseEntity<Object> response = adaptor.sendPUT(new ApiContext(path, "targetDataModel", uuid, null, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        mockServer.verify();
    }

    /**
     * sendDeleteメソッドのテスト response headerにX-tracking, linkが含まれる場合
     */
    @Test
    public void testSendDelete_withHeader() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String query = "query";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.DELETE))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent().header("X-Tracking", "test").header("link", "link"));

        // Act
        ResponseEntity<Void> response = adaptor.sendDELETE_Void(new ApiContext(path, null, uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        assertEquals(response.getHeaders().get("X-Tracking").get(0), "test");
        assertEquals(response.getHeaders().get("Link").get(0), "link"); 
        mockServer.verify();
    }
    /**
     * sendDeleteメソッドのテスト response headerにX-tracking, linkが含まれない場合
     */
    @Test
    public void testSendDelete_noHeader() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String query = "query";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.DELETE))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent());

        // Act
        ResponseEntity<Void> response = adaptor.sendDELETE_Void(new ApiContext(path, null, uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        assertEquals(response.getHeaders().containsKey("Link"), false);
        mockServer.verify();
    }

    /**
     * sendDeleteメソッドのテスト クエリに[]が含まれている場合
     */
    @Test
    public void testSendDelete_withQuery_brackets() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String query = "query[]";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.DELETE))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent());

        // Act
        ResponseEntity<Void> response = adaptor.sendDELETE_Void(new ApiContext(path, null, uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        mockServer.verify();
    }

    /**
     * sendDeleteメソッドのテスト クエリがない場合
     */
    @Test
    public void testSendDelete_noQuery() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel"))
                  .andExpect(method(HttpMethod.DELETE))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent());

        // Act
        ResponseEntity<Void> response = adaptor.sendDELETE_Void(new ApiContext(path, null, uuid, null, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        mockServer.verify();
    }

    /**
     * sendPostメソッドのテスト response headerにX-tracking, linkが含まれる場合
     */
    @Test
    public void testSendPost_withHeader() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String query = "query";
        String path = "/targetDataModel";
        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.POST))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON).header("X-Tracking", "test").header("link", "link"));

        // Act
        ResponseEntity<Object> response = adaptor.sendPOST(new ApiContext(path, "targetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        assertEquals(response.getHeaders().get("X-Tracking").get(0), "test");
        assertEquals(response.getHeaders().get("Link").get(0), "link"); 
        mockServer.verify();
    }

    
    /**
     * sendPostメソッドのテスト response headerにX-tracking, linkが含まれない場合
     */
    @Test
    public void testSendPost_noHeader() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String query = "query";
        String path = "/targetDataModel";
        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.POST))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // Act
        ResponseEntity<Object> response = adaptor.sendPOST(new ApiContext(path, "tarrgetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString()); 
        assertEquals(response.getHeaders().containsKey("Link"), false);
        mockServer.verify();
    }

    /**
     * sendPostメソッドのテスト クエリに[]が含まれている場合
     */
    @Test
    public void testSendPost_withQuery_brackets() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String query = "query[]";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.POST))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // Act
        ResponseEntity<Object> response = adaptor.sendPOST(new ApiContext(path, "targetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        mockServer.verify();
    }

    /**
     * sendPostメソッドのテスト クエリがない場合
     */
    @Test
    public void testSendPost_noQuery() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String expectedResponse = "{\"key\":\"value\"}"; // Replace with actual expected response
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel"))
                  .andExpect(method(HttpMethod.POST))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withSuccess(expectedResponse, MediaType.APPLICATION_JSON));

        // Act
        ResponseEntity<Object> response = adaptor.sendPOST(new ApiContext(path, "targetDataModel", uuid, null, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals("{key=value}", response.getBody().toString());
        mockServer.verify();
    }

    /**
     * sendPut_Void（返り値がない場合）メソッドのテスト クエリがある場合
     */
    @Test
    public void testSendPut_Void_withHeader() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String query = "query";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.PUT))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent());

        // Act
        ResponseEntity<Void> response = adaptor.sendPUT_Void(new ApiContext(path, "targetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        mockServer.verify();
    }

    /**
     * sendPut_Void（返り値がない場合）メソッドのテスト クエリに[]が含まれている場合
     */
    @Test
    public void testSendPut_Void_withQuery_brackets() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String query = "query[]";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.PUT))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent());

        // Act
        ResponseEntity<Void> response = adaptor.sendPUT_Void(new ApiContext(path, "targetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        mockServer.verify();
    }

    /**
     * sendPut_Void（返り値がない場合）メソッドのテスト クエリがない場合
     */
    @Test
    public void testSendPut_Void_noQuery() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel"))
                  .andExpect(method(HttpMethod.PUT))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent());

        // Act
        ResponseEntity<Void> response = adaptor.sendPUT_Void(new ApiContext(path, "targetDataModel", uuid, null, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        mockServer.verify();
    }

    /**
     * sendPost_Void（返り値がない場合）メソッドのテスト クエリがある場合
     */
    @Test
    public void testSendPost_Void_withHeader() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String query = "query";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.POST))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent());

        // Act
        ResponseEntity<Void> response = adaptor.sendPOST_Void(new ApiContext(path, "targetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        mockServer.verify();
    }

    /**
     * sendPost_Void（返り値がない場合）メソッドのテスト クエリに[]が含まれている場合
     */
    @Test
    public void testSendPost_Void_withQuery_brackets() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String query = "query[]";
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel?" + query))
                  .andExpect(method(HttpMethod.POST))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent());

        // Act
        ResponseEntity<Void> response = adaptor.sendPOST_Void(new ApiContext(path, "targetDataModel", uuid, query, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        mockServer.verify();
    }

    /**
     * sendPost_Void（返り値がない場合）メソッドのテスト クエリがない場合
     */
    @Test
    public void testSendPost_Void_noQuery() {
        // Mock setting
        UUID uuid = UUID.randomUUID();
        String path = "/targetDataModel";

        when(mockRestClient.build()).thenReturn(restClient.build());
        mockServer.expect(requestTo("http://localhost:8080/targetDataModel"))
                  .andExpect(method(HttpMethod.POST))
                  .andExpect(header("apiKey", "testapiKey"))
                  .andExpect(header("Authorization", "Bearer token"))
                  .andRespond(withNoContent());

        // Act
        ResponseEntity<Void> response = adaptor.sendPOST_Void(new ApiContext(path, "targetDataModel", uuid, null, "Bearer token"));

        // Assert
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatusCode.valueOf(204));
        mockServer.verify();
    }
}
