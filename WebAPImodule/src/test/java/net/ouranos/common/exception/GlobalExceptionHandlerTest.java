package net.ouranos.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.HttpInputMessage;
import org.springframework.mock.http.MockHttpInputMessage;

import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private WebRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(request.getHeader("X-transaction")).thenReturn("transaction");
    }

    /**
     * handleValidationException メソッドのテスト
     */
    @Test
    void testhandleValidationException() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        FieldError fieldError = mock(FieldError.class);
        when(ex.getFieldErrors()).thenReturn(List.of(fieldError));
        when(fieldError.getObjectName()).thenReturn("dataspace");
        when(fieldError.getField()).thenReturn("field");
        when(fieldError.getDefaultMessage()).thenReturn("testmessage");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
        ErrorResponse expectedResponse = new ErrorResponse("[dataspace] BadRequest", "dataspace field testmessage,", expectedTimestamp);

        ResponseEntity<ErrorResponse> actualResponse = globalExceptionHandler.handleValidataionException(ex, request);

        assertEquals(expectedResponse.message(), actualResponse.getBody().message());
        assertEquals(expectedResponse.code(), actualResponse.getBody().code());
        assertTrue(actualResponse.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleMismatchException メソッドのテスト
     */
    @Test
    void testhandleMismatchException() {
        TypeMismatchException ex = mock(TypeMismatchException.class);
        IllegalArgumentException illegalArgumentException = mock(IllegalArgumentException.class);
        when(ex.getMostSpecificCause()).thenReturn(illegalArgumentException);
        when(illegalArgumentException.getMessage()).thenReturn("testmessage");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
        ErrorResponse expectedResponse = new ErrorResponse("[dataspace] BadRequest", "testmessage", expectedTimestamp);

        ResponseEntity<ErrorResponse> actualResponse = globalExceptionHandler.handleMismatchException(ex, request);

        assertEquals(expectedResponse.message(), actualResponse.getBody().message());
        assertEquals(expectedResponse.code(), actualResponse.getBody().code());
        assertTrue(actualResponse.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleSizeValidataionException メソッドのテスト
     */
    @Test
    void testhandleSizeValidataionException() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        when(ex.getMessage()).thenReturn("testmessage");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
        ErrorResponse expectedResponse = new ErrorResponse("[dataspace] BadRequest", "testmessage", expectedTimestamp);

        ResponseEntity<ErrorResponse> actualResponse = globalExceptionHandler.handleSizeValidataionException(ex, request);

        assertEquals(expectedResponse.message(), actualResponse.getBody().message());
        assertEquals(expectedResponse.code(), actualResponse.getBody().code());
        assertTrue(actualResponse.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleMissingRequestValueException メソッドのテスト
     */
    @Test
    void testhandleMissingRequestValueException() {
        MissingRequestValueException ex = mock(MissingRequestValueException.class);
        when(ex.getMessage()).thenReturn("testmessage");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
        ErrorResponse expectedResponse = new ErrorResponse("[dataspace] BadRequest", "testmessage", expectedTimestamp);

        ResponseEntity<ErrorResponse> actualResponse = globalExceptionHandler.handleMissingRequestValueException(ex, request);

        assertEquals(expectedResponse.message(), actualResponse.getBody().message());
        assertEquals(expectedResponse.code(), actualResponse.getBody().code());
        assertTrue(actualResponse.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 18)));
    }

    /**
     * handleVerifyTokenIntrospectionFalseExceptionのテストメソッド
     */
    @Test
    public void testhandleVerifyTokenIntrospectionFalseException() {
        VerifyTokenIntrospectionFalseException ex = new VerifyTokenIntrospectionFalseException();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
    
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleVerifyTokenIntrospectionFalseException(ex, request);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(401), response.getStatusCode());
        assertEquals("[auth] Unauthorized", response.getBody().code());
        assertEquals("Invalid or expired token", response.getBody().message());
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 19)));
    }

    /**
     * handleClientErrorExceptionのテストメソッド
     */
    @Test
    public void testHandleClientErrorExceptionelse() {
        HttpClientErrorException ex = new HttpClientErrorException(HttpStatusCode.valueOf(400), "[dataspace] BadRequest", "{\"code\":\"[dataspace] BadRequest\",\"message\":\"Bad Request\",\"detail\":\"timeStamp: 2024-08-07T16:11:47.528+0900\"}".getBytes(), null);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleClientErrorException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        String responsestr = response.getBody().toString();
        assertEquals("{\"code\":\"[dataspace] BadRequest\"", responsestr.split(",")[0]);
        assertEquals("\"message\":\"Bad Request\"", responsestr.split(",")[1]);
        assertEquals("\"detail\":\"timeStamp: 2024-08-07T16:11:47.528+0900\"}", responsestr.split(",")[2]);
    }

    /**
     * handleServerErrorExceptionのテストメソッド
     */
    @Test
    public void testHandleServerErrorExceptionelse() {
        HttpServerErrorException ex = new HttpServerErrorException(HttpStatusCode.valueOf(500), "[dataspace] BadRequest", "{\"code\":\"[dataspace] BadRequest\",\"message\":\"Bad Request\",\"detail\":\"timeStamp: 2024-08-07T16:11:47.528+0900\"}".getBytes(), null);

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleServerErrorException(ex, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
        String responsestr = response.getBody().toString();
        assertEquals("{\"code\":\"[dataspace] BadRequest\"", responsestr.split(",")[0]);
        assertEquals("\"message\":\"Bad Request\"", responsestr.split(",")[1]);
        assertEquals("\"detail\":\"timeStamp: 2024-08-07T16:11:47.528+0900\"}", responsestr.split(",")[2]);
    }

    /**
     * handleResourceAccessExceptionのテストメソッド
     */
    @Test
    public void testhandleResourceAccessException() {
        ResourceAccessException ex = new ResourceAccessException("testmessage");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
    
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleResourceAccessException(ex, request);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(503), response.getStatusCode());
        assertEquals("[dataspace] ServiceUnavailable", response.getBody().code());
        assertEquals("Unexpected error occurred in outer service", response.getBody().message());
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 19)));
    }

    /**
     * handleNoResourceFoundException
     */
    @Test
    public void testhandleNoResourceFoundException() {
        NoResourceFoundException ex = new NoResourceFoundException(HttpMethod.DELETE, "testmessage");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
    
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleNoResourceFoundException(ex, request);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
        assertEquals("[dataspace] NotFound", response.getBody().code());
        assertEquals("Endpoint Not Found", response.getBody().message());
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 19)));
    }

    /**
     * handleHttpMessageNotReadableExceptionのテストメソッド
     */
    @Test
    public void testhandleHttpMessageNotReadableException() {
        HttpInputMessage inputMessage = new MockHttpInputMessage("testmessage".getBytes());
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("testmessage", inputMessage);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
    
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotReadableException(ex, request);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
        assertEquals("[dataspace] BadRequest", response.getBody().code());
        assertEquals("Invalid request body", response.getBody().message());
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 19)));
    }

    @Test
    public void testhandleHttpMessageNotWritableException() {
        HttpMessageNotWritableException ex = new HttpMessageNotWritableException("testmessage");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String expectedTimestamp = sdf.format(timestamp);
    
        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleHttpMessageNotWritableException(ex, request);
    
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatusCode.valueOf(500), response.getStatusCode());
        assertEquals("[dataspace] InternalServerError", response.getBody().code());
        assertEquals("Unexpected error occurred", response.getBody().message());
        assertTrue(response.getBody().detail().contains("timeStamp: " + expectedTimestamp.substring(0, 19)));
    }
}