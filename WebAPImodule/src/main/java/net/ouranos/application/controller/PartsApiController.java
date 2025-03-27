package net.ouranos.application.controller;

import net.ouranos.domain.model.Parts;
import net.ouranos.transform.Transform;

import java.util.UUID;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.context.request.NativeWebRequest;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Objects;
import jakarta.annotation.Generated;

import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import net.ouranos.adaptor.Adaptor;
import net.ouranos.common.component.TokenIntrospection;
import net.ouranos.common.component.TrackingHeaderBean;
import net.ouranos.common.context.ApiContext;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-03-10T18:31:43.516109700+09:00[Asia/Tokyo]", comments = "Generator version: 7.6.0")
@Validated
@Tag(name = "データ流通システム", description = "the データ流通システム API")
@Slf4j
@RestController
@RequestMapping("/")
public class PartsApiController implements PartsApi {

    @Value("${api.parts.url}")
    private String apiUrl;

    private final Optional<Transform> transform;
    private final Adaptor<Object> adaptor;
    private final TokenIntrospection tokenIntrospection;
    private final TrackingHeaderBean trackingHeaderBean;

    public PartsApiController(@Qualifier("partsTransform") Optional<Transform> transform, Adaptor<Object> adaptor, TokenIntrospection tokenIntrospection, TrackingHeaderBean trackingHeaderBean) {
        this.transform = transform;
        this.adaptor = adaptor;
        this.tokenIntrospection = tokenIntrospection;
        this.trackingHeaderBean = trackingHeaderBean;
    }

    @Override
    public ResponseEntity<Void> partsDelete(
      UUID xTracking
    , String id
    ) {
        //extract token from request header
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("Authorization");

        //extract queryParam
        String query = request.getQueryString();

        //verify token
        tokenIntrospection.verifyToken(token);

        //get or issue tracking id
        UUID trackingUuid = this.trackingHeaderBean.getUUID();

        log.info("Starts a DELETE process. dataModel = parts, tracking = {}", xTracking);
        log.info("Execute the adaptor. adaptor = {}, tracking = {}",this.adaptor.getClass(), trackingUuid);
        ApiContext apiContext = new ApiContext(apiUrl, null, trackingUuid, query, token);

        // Transform the ApiContext
        ApiContext transformedApiContext = this.transform.map(t -> t.encodeDELETE(apiContext)).orElse(apiContext);

        // Execute the adaptor
        ResponseEntity<Void> response = this.adaptor.sendDELETE_Void(transformedApiContext);
        log.info("Send a DELETE request completed. tracking = {}", trackingUuid);

        // Transform the response
        ResponseEntity<Void> result = this.transform.map(t -> t.decodeDELETE(response)).orElse(response);

        return result;
    }

    public ResponseEntity<Object> partsGet(
      UUID xTracking
    ) {
        //extract token from request header
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("Authorization");

        //extract queryParam
        String query = request.getQueryString();

        //verify token
        tokenIntrospection.verifyToken(token);

        //get or issue tracking id
        UUID trackingUuid = this.trackingHeaderBean.getUUID();

        log.info("Starts a GET process. dataModel = parts, tracking = {}", xTracking);
        log.info("Execute the adaptor. adaptor = {}, tracking = {}",this.adaptor.getClass(), trackingUuid);
        ApiContext apiContext = new ApiContext(apiUrl, null, trackingUuid, query, token);

        // Transform the ApiContext
        ApiContext transformedApiContext = this.transform.map(t -> t.encodeGET(apiContext)).orElse(apiContext);

        // Execute the adaptor
        ResponseEntity<Object> response = this.adaptor.sendGET(transformedApiContext);
        log.info("Send a GET request completed. tracking = {}", trackingUuid);

        // Transform the response
        ResponseEntity<Object> result = this.transform.map(t -> t.decodeGET(response)).orElse(response);

        return result;
    }

    public ResponseEntity<Object> partsPost(
      UUID xTracking
    , Parts parts
    ) {
        //extract token from request header
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("Authorization");

        //extract queryParam
        String query = request.getQueryString();

        //verify token
        tokenIntrospection.verifyToken(token);

        //get or issue tracking id
        UUID trackingUuid = this.trackingHeaderBean.getUUID();

        log.info("Starts a POST process. dataModel = parts, tracking = {}", xTracking);
        log.info("Execute the adaptor. adaptor = {}, tracking = {}",this.adaptor.getClass(), trackingUuid);
        ApiContext apiContext = new ApiContext(apiUrl, parts, trackingUuid, query, token);

        // Transform the ApiContext
        ApiContext transformedApiContext = this.transform.map(t -> t.encodePOST(apiContext)).orElse(apiContext);

        // Execute the adaptor
        ResponseEntity<Object> response = this.adaptor.sendPOST(transformedApiContext);
        log.info("Send a POST request completed. tracking = {}", trackingUuid);

        // Transform the response
        ResponseEntity<Object> result = this.transform.map(t -> t.decodePOST(response)).orElse(response);

        return result;
    }

    public ResponseEntity<Object> partsPut(
      UUID xTracking
    , Parts parts
    ) {
        //extract token from request header
        HttpServletRequest request = ((ServletRequestAttributes) Objects
            .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("Authorization");

        //extract queryParam
        String query = request.getQueryString();

        //verify token
        tokenIntrospection.verifyToken(token);

        //get or issue tracking id
        UUID trackingUuid = this.trackingHeaderBean.getUUID();

        log.info("Starts a PUT process. dataModel = parts, tracking = {}", xTracking);
        log.info("Execute the adaptor. adaptor = {}, tracking = {}",this.adaptor.getClass(), trackingUuid);
        ApiContext apiContext = new ApiContext(apiUrl, parts, trackingUuid, query, token);

        // Transform the ApiContext
        ApiContext transformedApiContext = this.transform.map(t -> t.encodePUT(apiContext)).orElse(apiContext);

        // Execute the adaptor
        ResponseEntity<Object> response = this.adaptor.sendPUT(transformedApiContext);
        log.info("Send a PUT request completed. tracking = {}", trackingUuid);

        // Transform the response
        ResponseEntity<Object> result = this.transform.map(t -> t.decodePUT(response)).orElse(response);

        return result;
    }

}
