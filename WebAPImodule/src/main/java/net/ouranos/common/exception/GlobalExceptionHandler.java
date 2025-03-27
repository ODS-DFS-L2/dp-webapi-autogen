package net.ouranos.common.exception;

import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.validation.ConstraintViolationException;

/**
 * 例外ハンドラ
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private enum SystemName{
        AUTH("auth"),
        DATASPACE("dataspace");

        String systemName;
        SystemName(String systemName) {
            this.systemName = systemName;
        }
      };


    /**
     * MethodArgumentNotValidExceptionの例外ハンドリング
     * Valid アノテーションが付けられた引数の検証が失敗した場合にスローされる例外
     * 400エラーレスポンスを作成して返却します。
     * 
     * @param ex MethodArgumentNotValidException
     * @param request WebRequest
     * @return エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidataionException(final MethodArgumentNotValidException ex, WebRequest request) {
        log.error("MethodArgumentNotValidException exception occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
        ex.printStackTrace();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str = sdf.format(timestamp);
        List<FieldError> fieldErrors = ex.getFieldErrors();
        String errorMessage = "";
        for (FieldError fieldError : fieldErrors) {
            errorMessage = errorMessage.concat(fieldError.getObjectName() + " " + fieldError.getField() + " " + fieldError.getDefaultMessage() + ",");
        }
        ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] BadRequest", errorMessage, "timeStamp: "+str);
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * TypeMismatchExceptionの例外ハンドリング
     * 型の不一致が発生した場合にスローされる例外
     * 400エラーを作成して返却します。
     * 
     * @param ex TypeMismatchException
     * @param request WebRequest
     * @return エラーレスポンス
     */
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMismatchException(final TypeMismatchException ex, WebRequest request) {
        log.error("TypeMismatchException exception occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
        ex.printStackTrace();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str = sdf.format(timestamp);
        String errorMessage = ex.getMostSpecificCause().getMessage();
        ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] BadRequest", errorMessage, "timeStamp: "+str);
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * ConstraintViolationExceptionの例外ハンドリング
     * size違反などの制約違反があった場合にスローされる例外
     * 400エラーを作成して返却します。
     * 
     * @param ex ConstraintViolationException
     * @param request WebRequest
     * @return エラーレスポンス
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleSizeValidataionException(final ConstraintViolationException ex, WebRequest request) {
        log.error("ConstraintViolationException occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
        ex.printStackTrace();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str = sdf.format(timestamp);
        String errorMessage = ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] BadRequest", errorMessage, "timeStamp: "+str);
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * MissingRequestValueExceptionの例外ハンドリング
     * リクエストパラメータが不足している場合にスローされる例外
     * 400エラーを作成して返却します。
     * 
     * @param ex MissingRequestValueException
     * @param request WebRequest
     * @return エラーレスポンス
     */
    @ExceptionHandler(MissingRequestValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMissingRequestValueException(final MissingRequestValueException ex, WebRequest request) {
        log.error("An exception occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
        ex.printStackTrace();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str = sdf.format(timestamp);
        String errorMessage = ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] BadRequest", errorMessage, "timeStamp: "+str);
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * VerifyTokenIntrospectionFalseException を処理します。
     *
     * @param ex VerifyTokenIntrospectionFalseException インスタンス
     * @param request WebRequest
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(VerifyTokenIntrospectionFalseException.class)
    public ResponseEntity<ErrorResponse> handleVerifyTokenIntrospectionFalseException(final  VerifyTokenIntrospectionFalseException ex, WebRequest request) {
      log.error("VerifyTokenIntrospectionFalseException occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
      ex.printStackTrace();
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String str = sdf.format(timestamp);
      ErrorResponse errorResponse = new ErrorResponse("["+SystemName.AUTH.systemName+"] Unauthorized", "Invalid or expired token","timeStamp: "+str);
      return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * HttpClientErrorException を処理します。
     * RestTemplateで4xx系のエラーが発生した場合に発生します。
     * エラーレスポンスをそのまま返します。
     *
     * @param ex HttpClientErrorException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleClientErrorException(final  HttpClientErrorException ex, WebRequest request) {
      log.error("HttpClientErrorException occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
      ex.printStackTrace();
      String responseBody = ex.getResponseBodyAsString();
      return ResponseEntity.status(ex.getStatusCode()).body(responseBody);
    }

    /**
     * HttpServerErrorException を処理します。
     * RestTemplateで5xx系のエラーが発生した場合に発生します。
     * エラーレスポンスをそのまま返します。
     *
     * @param ex HttpServerErrorException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<Object> handleServerErrorException(final  HttpServerErrorException ex, WebRequest request) {
      log.error("HttpServerErrorException occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
      ex.printStackTrace();
      String responseBody = ex.getResponseBodyAsString();
      return ResponseEntity.status(ex.getStatusCode()).body(responseBody);
    }

    /**
     * ResourceAccessException を処理します。
     * 接続先とのコネクション接続ができない場合に発生します。
     * 
     * @param ex バックエンドサーバ利用不可例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(ResourceAccessException.class)
    public  ResponseEntity<ErrorResponse> handleResourceAccessException(final ResourceAccessException ex, WebRequest request) {
        log.error("ResourceAccessException occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
        ex.printStackTrace();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String str = sdf.format(timestamp);
        ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] ServiceUnavailable", "Unexpected error occurred in outer service", "timeStamp: "+str);
        return new ResponseEntity<ErrorResponse>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * NoResourceFoundException を処理します。
     * 存在しないResourceにアクセスした場合に発生します。
     * 
     * @param ex NoResourceFoundException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(final NoResourceFoundException ex, WebRequest request) {
      log.error("NoResourceFoundException occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
      ex.printStackTrace();
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String str = sdf.format(timestamp);
      ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] NotFound", "Endpoint Not Found", "timeStamp: "+str);
      return ResponseEntity.status(404).body(errorResponse);
    }

    /**
     * HttpMessageNotReadableException を処理します。
     * リクエストボディが不正な場合に発生します。
     * 
     * @param ex HttpMessageNotReadableException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException ex, WebRequest request) {
      log.error("HttpMessageNotReadableException occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
      ex.printStackTrace();
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String str = sdf.format(timestamp);
      ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] BadRequest", "Invalid request body", "timeStamp: "+str);
      return ResponseEntity.status(400).body(errorResponse);
    }

    /**
     * HttpMessageNotWritableException を処理します。
     * レスポンスボディの書き込み失敗エラー（例えばJavaオブジェクトをHTTPレスポンスのボディにシリアライズできない時など）
     * 
     * @param ex HttpMessageNotWritableException インスタンス
     * @return エラーレスポンスを含む ResponseEntity オブジェクト
     */
    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotWritableException(final HttpMessageNotWritableException ex, WebRequest request) {
      log.error("HttpMessageNotWritableException occured. {}, Tracking = {}", ex.getMessage(), request.getHeader("X-Tracking"));
      ex.printStackTrace();
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
      String str = sdf.format(timestamp);
      ErrorResponse errorResponse = new ErrorResponse("["+SystemName.DATASPACE.systemName+"] InternalServerError", "Unexpected error occurred", "timeStamp: "+str);
      return ResponseEntity.status(500).body(errorResponse);
    }
}
