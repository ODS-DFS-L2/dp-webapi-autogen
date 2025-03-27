package net.ouranos.application.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import net.ouranos.adaptor.Adaptor;
import net.ouranos.common.component.TokenIntrospection;
import net.ouranos.common.component.TrackingHeaderBean;
import net.ouranos.common.context.ApiContext;
import net.ouranos.domain.model.Parts;
import net.ouranos.transform.Transform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;

public class PartsApiControllerTest {

    /**
     * {@link Transform} のモック。
     */
    @Mock
    private Transform transform;

    /**
     * {@link Adaptor} のモック。
     */
    @Mock
    private Adaptor<Object> adaptor;

    /**
     * {@link TokenIntrospection} のモック。
     */
    @Mock
    private TokenIntrospection tokenIntrospection;

    /**
     * {@link TrackingHeaderBean} のモック。
     */
    @Mock
    private TrackingHeaderBean trackingHeaderBean;
    /**
     * {@link HttpServletRequest} のモック。
     */
    @Mock
    private HttpServletRequest request;

    /**
     * {@link ServletRequestAttributes} のモック。
     */
    @Mock
    private ServletRequestAttributes servletRequestAttributes;


    /**
     * テスト対象クラス{@link PartsApiController} 。
     */
    @InjectMocks
    private PartsApiController partsApiController;

    private UUID xTracking;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        xTracking = UUID.randomUUID();
        RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    }

    @Test
    void testDelete() throws Exception {
        // テストデータ
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().header("X-Tracking", xTracking.toString()).build();
        ApiContext apiContext = new ApiContext("/parts", null, xTracking, "query", "token");
        
        // モックの設定
        when(trackingHeaderBean.getUUID()).thenReturn(xTracking);
        when(adaptor.sendDELETE_Void(any(ApiContext.class))).thenReturn(responseEntity);
        when(transform.encodeDELETE(any(ApiContext.class))).thenReturn(apiContext);
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("Authorization")).thenReturn("token");
        when(transform.decodeDELETE(responseEntity)).thenReturn(responseEntity);

        // 実行
        partsApiController = new PartsApiController(Optional.of(transform), adaptor, tokenIntrospection, trackingHeaderBean);
        ResponseEntity<Void> response = partsApiController.partsDelete(xTracking, "query");

        // 検証
        assertEquals(HttpStatus.OK, response.getStatusCode(), "ステータスコードが200 OKであること");
        assertEquals(null, response.getBody(), "レスポンスボディが期待通りであること");
        verify(transform, times(1)).encodeDELETE(any(ApiContext.class));
        verify(tokenIntrospection, times(1)).verifyToken("token");
        verify(trackingHeaderBean, times(1)).getUUID();
        verify(adaptor, times(1)).sendDELETE_Void(any(ApiContext.class));
        verify(transform, times(1)).decodeDELETE(responseEntity);
    }

    @Test
    void testGet() {
        // テストデータ
        Parts parts = new Parts();
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(parts);
        ApiContext apiContext = new ApiContext("/parts", null, xTracking, "query", "token");

        // モックの設定
        when(trackingHeaderBean.getUUID()).thenReturn(xTracking);
        when(adaptor.sendGET(any(ApiContext.class))).thenReturn(responseEntity);
        when(transform.encodeGET(any(ApiContext.class))).thenReturn(apiContext);
        when(transform.decodeGET(responseEntity)).thenReturn(responseEntity);
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("Authorization")).thenReturn("token");

        // 実行
        partsApiController = new PartsApiController(Optional.of(transform), adaptor, tokenIntrospection, trackingHeaderBean);
        ResponseEntity<Object> response = partsApiController.partsGet(xTracking);
        
        // 検証
        assertEquals(ResponseEntity.ok(parts), response, "レスポンスが期待通りであること");
        verify(transform, times(1)).encodeGET(any(ApiContext.class));
        verify(transform, times(1)).decodeGET(responseEntity);
        verify(tokenIntrospection, times(1)).verifyToken("token");
        verify(trackingHeaderBean, times(1)).getUUID();
        verify(adaptor, times(1)).sendGET(any(ApiContext.class));
    }

    @Test
    void testPut() {
        // テストデータ
        Parts parts = new Parts();
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(parts);
        ApiContext apiContext = new ApiContext("/parts", parts, xTracking, "query", "token");

        // モックの設定
        when(trackingHeaderBean.getUUID()).thenReturn(xTracking);
        when(transform.encodePUT(any(ApiContext.class))).thenReturn(apiContext);
        when(adaptor.sendPUT(any(ApiContext.class))).thenReturn(responseEntity);
        when(transform.decodePUT(responseEntity)).thenReturn(responseEntity);
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("Authorization")).thenReturn("token");

        // 実行
        partsApiController = new PartsApiController(Optional.of(transform), adaptor, tokenIntrospection, trackingHeaderBean);
        ResponseEntity<Object> response = partsApiController.partsPut(xTracking, parts);

        // 検証
        assertEquals(ResponseEntity.ok(parts), response, "レスポンスが期待通りであること");
        verify(transform, times(1)).encodePUT(any(ApiContext.class));
        verify(transform, times(1)).decodePUT(responseEntity);
        verify(tokenIntrospection, times(1)).verifyToken("token");
        verify(trackingHeaderBean, times(1)).getUUID();
        verify(adaptor, times(1)).sendPUT(any(ApiContext.class));
    }

    @Test
    void testPost() {
        // テストデータ
        Parts parts = new Parts();
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(parts);
        ApiContext apiContext = new ApiContext("/parts", parts, xTracking, "query", "token");

        // モックの設定
        when(trackingHeaderBean.getUUID()).thenReturn(xTracking);
        when(transform.encodePOST(any(ApiContext.class))).thenReturn(apiContext);
        when(adaptor.sendPOST(any(ApiContext.class))).thenReturn(responseEntity);
        when(transform.decodePOST(responseEntity)).thenReturn(responseEntity);
        when(servletRequestAttributes.getRequest()).thenReturn(request);
        when(request.getHeader("Authorization")).thenReturn("token");

        // 実行
        partsApiController = new PartsApiController(Optional.of(transform), adaptor, tokenIntrospection, trackingHeaderBean);
        ResponseEntity<Object> response = partsApiController.partsPost(xTracking, parts);

        // 検証
        assertEquals(ResponseEntity.ok(parts), response, "レスポンスが期待通りであること");
        verify(transform, times(1)).encodePOST(any(ApiContext.class));
        verify(transform, times(1)).decodePOST(responseEntity);
        verify(tokenIntrospection, times(1)).verifyToken("token");
        verify(trackingHeaderBean, times(1)).getUUID();
        verify(adaptor, times(1)).sendPOST(any(ApiContext.class));
    }

}