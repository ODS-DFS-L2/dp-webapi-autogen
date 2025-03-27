package net.ouranos.transform;

import org.springframework.http.ResponseEntity;

import net.ouranos.common.context.ApiContext;

public interface Transform {

    
    default ApiContext encodeDELETE(ApiContext apiContext){
        return apiContext;
    };

    default ResponseEntity<Void> decodeDELETE(ResponseEntity<Void> responseEntity){
        return responseEntity;
    };

    default ApiContext encodeGET(ApiContext apiContext){
        return apiContext;
    };

    default ResponseEntity<Object> decodeGET(ResponseEntity<Object> responseEntity){
        return responseEntity;
    };

    default ApiContext encodePUT(ApiContext apiContext){
        return apiContext;
    };

    default ResponseEntity<Object> decodePUT(ResponseEntity<Object> responseEntity){
        return responseEntity;
    };

    default ResponseEntity<Void> decodePUT_Void(ResponseEntity<Void> responseEntity){
        return responseEntity;
    };

    default ApiContext encodePOST(ApiContext apiContext){
        return apiContext;
    };

    default ResponseEntity<Object> decodePOST(ResponseEntity<Object> responseEntity){
        return responseEntity;
    };

    default ResponseEntity<Void> decodePOST_Void(ResponseEntity<Void> responseEntity){
        return responseEntity;
    };
}
