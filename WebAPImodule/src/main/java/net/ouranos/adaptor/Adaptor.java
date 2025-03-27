package net.ouranos.adaptor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.ouranos.common.context.ApiContext;

/**
 * Adaptorクラスは、データモデルに対してGET、PUT、DELETE、およびそれぞれのVoid型のリクエストを送信するためのメソッドを定義します。
 *
 * @param <T> データモデルの型
 */
@Component
@Slf4j
public class Adaptor<T> {

    /**
     * 環境変数からapiKeyを取得し保持するフィールド。
    */
    @Value("${apiKey}")
    private String apiKey;

    /**
     * 環境変数からbaseUrlを取得し保持するフィールド。
    */
    @Value("${trustsystem.base-url}")
    private String baseUrl;

    /**
     * RestClientのビルダーを保持するフィールド。
    */
    @Autowired
    private RestClient.Builder restClient;

    private List<String> excludedHeaders;
    // "excluded-headers.txt" に記載のある文字列を含むヘッダーを除外する
    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("excluded-headers.txt");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                excludedHeaders = reader.lines().collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Failed to load excluded headers", e);
            excludedHeaders = new ArrayList<>();
        }
    }

    /**
     * リクエストヘッダーから"x-"で始まるヘッダーを抽出します。
     *
     * @return "x-"で始まるヘッダーのMultiValueMap
     */
    private MultiValueMap<String, String> getFilteredHeaders() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
            .getRequestAttributes()).getRequest();
        HttpHeaders headers = new HttpHeaders();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.add(headerName, request.getHeader(headerName));
        });

        MultiValueMap<String, String> xheaders = new LinkedMultiValueMap<>();
        headers.forEach((key, values) -> {
            if (key.startsWith("x-")) {
                xheaders.put(key, values);
            }
        });

        excludedHeaders.forEach(excludedHeader -> {
            xheaders.keySet().removeIf(key -> key.toLowerCase().contains(excludedHeader.toLowerCase()));
        });

        return xheaders;
    }


    /**
     * 指定されたデータモデルに対してGETリクエストを送信します。
     *
     * @param targetDataModel 取得対象のデータモデル
     * @param tracking トラッキングID
     * @return サーバーからのレスポンス
     */
    public ResponseEntity<Object> sendGET(ApiContext apiContext){
        String pathName = apiContext.apiUrl();
        UUID tracking = apiContext.xTracking();
        String queryParam = apiContext.queryParam();
        String token = apiContext.token();
        log.info("Send GET to {} service. Tracking = {}", pathName, tracking);
        MultiValueMap<String, String> xheaders = getFilteredHeaders();
        ResponseEntity<Object> result;
        if(queryParam == null) {
            String url = baseUrl + pathName;
            result = this.restClient.build().get()
            .uri(url)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .retrieve()
            .toEntity(Object.class);
        } else if(queryParam.contains("[") && queryParam.contains("]")){
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build().toUri();
            result = this.restClient.build().get()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .retrieve()
            .toEntity(Object.class);
        } else {
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build(true).toUri();
            result = this.restClient.build().get()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .retrieve()
            .toEntity(Object.class);
        }
        HttpStatusCode statusCode = result.getStatusCode();
        Object body = result.getBody();

        // "x-" と "link" を含むレスポンスヘッダーを抽出
        HttpHeaders filteredHeaders = new HttpHeaders();
        result.getHeaders().forEach((key, values) -> {
            if (key.toLowerCase().startsWith("x-") || key.toLowerCase().contains("link")) {
                filteredHeaders.put(key, values);
            }
        });
        // "x-"を含むレスポンスヘッダーで抽出する必要のない（"excluded-headers.txt" に記載のある文字列を含む）ヘッダーを削除
        excludedHeaders.forEach(excludedHeader -> {
            filteredHeaders.keySet().removeIf(key -> key.toLowerCase().contains(excludedHeader.toLowerCase()));
        });
        return new ResponseEntity<>(body, filteredHeaders, statusCode);
    }

    /**
     * 指定されたデータモデルに対してPUTリクエストを送信します。
     *
     * @param dataModel 送信するデータモデル
     * @param tracking トラッキングID
     * @return サーバーからのレスポンス
     */
    public ResponseEntity<Object> sendPUT(ApiContext apiContext){
        String pathName = apiContext.apiUrl();
        UUID tracking = apiContext.xTracking();
        Object dataModel = apiContext.datamodel();
        String queryParam = apiContext.queryParam();
        String token = apiContext.token();
        log.info("Send PUT to {} service. Tracking = {}", pathName, tracking);
        MultiValueMap<String, String> xheaders = getFilteredHeaders();
        ResponseEntity<Object> result;
        if(queryParam == null) {
            String url = baseUrl + pathName;
            result = this.restClient.build().put()
            .uri(url)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toEntity(Object.class);
        } else if(queryParam.contains("[") && queryParam.contains("]")){
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build().toUri();
            result = this.restClient.build().put()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toEntity(Object.class);
        } else {
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build(true).toUri();
            result = this.restClient.build().put()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toEntity(Object.class);
        }
        HttpStatusCode statusCode = result.getStatusCode();
        Object body = result.getBody();
        // "x-" と "link" を含むレスポンスヘッダーを抽出
        HttpHeaders filteredHeaders = new HttpHeaders();
        result.getHeaders().forEach((key, values) -> {
            if (key.toLowerCase().startsWith("x-") || key.toLowerCase().contains("link")) {
                filteredHeaders.put(key, values);
            }
        });
        // "x-"を含むレスポンスヘッダーで抽出する必要のない（"excluded-headers.txt" に記載のある文字列を含む）ヘッダーを削除
        excludedHeaders.forEach(excludedHeader -> {
            filteredHeaders.keySet().removeIf(key -> key.toLowerCase().contains(excludedHeader.toLowerCase()));
        });
        return new ResponseEntity<>(body, filteredHeaders, statusCode);
    }

    /**
     * 指定されたデータモデルに対してPUT(Void型）リクエストを送信します。
     *
     * @param dataModel 送信するデータモデル
     * @param tracking トラッキングID
     * @return サーバーからのレスポンス
     */
    public ResponseEntity<Void> sendPUT_Void(ApiContext apiContext){
        String pathName = apiContext.apiUrl();
        UUID tracking = apiContext.xTracking();
        Object dataModel = apiContext.datamodel();
        String queryParam = apiContext.queryParam();
        String token = apiContext.token();
        log.info("Send PUT to {} service. Tracking = {}", pathName, tracking);
        MultiValueMap<String, String> xheaders = getFilteredHeaders();
        ResponseEntity<Void> result;
        if(queryParam == null) {
            String url = baseUrl + pathName;
            result = this.restClient.build().put()
            .uri(url)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toBodilessEntity();
        } else if(queryParam.contains("[") && queryParam.contains("]")){
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build().toUri();
            result = this.restClient.build().put()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toBodilessEntity();
        } else {
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build(true).toUri();
            result = this.restClient.build().put()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toBodilessEntity();
        }
        HttpStatusCode statusCode = result.getStatusCode();

        // "x-" と "link" を含むレスポンスヘッダーを抽出
        HttpHeaders filteredHeaders = new HttpHeaders();
        result.getHeaders().forEach((key, values) -> {
            if (key.toLowerCase().startsWith("x-") || key.toLowerCase().contains("link")) {
                filteredHeaders.put(key, values);
            }
        });
        // "x-"を含むレスポンスヘッダーで抽出する必要のない（"excluded-headers.txt" に記載のある文字列を含む）ヘッダーを削除
        excludedHeaders.forEach(excludedHeader -> {
            filteredHeaders.keySet().removeIf(key -> key.toLowerCase().contains(excludedHeader.toLowerCase()));
        });

        return new ResponseEntity<>(filteredHeaders, statusCode);
    }
     
    /**
     * 指定されたデータモデルに対してDELETEリクエストを送信します。
     *
     * @param id 削除対象のデータモデルのデータID
     * @param tracking トラッキングID
     * @return サーバーからのレスポンス
     */
    public ResponseEntity<Void> sendDELETE_Void(ApiContext apiContext){
        String pathName = apiContext.apiUrl();
        UUID tracking = apiContext.xTracking();
        String queryParam = apiContext.queryParam();
        String token = apiContext.token();
        log.info("Send DELETE to {} service. Tracking = {}", pathName, tracking);
        MultiValueMap<String, String> xheaders = getFilteredHeaders();
        ResponseEntity<Void> result;
        if(queryParam == null) {
            String url = baseUrl + pathName;
            result = this.restClient.build().delete()
            .uri(url)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .retrieve()
            .toBodilessEntity();
        } else if(queryParam.contains("[") && queryParam.contains("]")){
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build().toUri();
            result = this.restClient.build().delete()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .retrieve()
            .toBodilessEntity();
        } else {
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build(true).toUri();
            result = this.restClient.build().delete()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .retrieve()
            .toBodilessEntity();
        }
        HttpStatusCode statusCode = result.getStatusCode();
        // "x-" と "link" を含むレスポンスヘッダーを抽出
        HttpHeaders filteredHeaders = new HttpHeaders();
        result.getHeaders().forEach((key, values) -> {
            if (key.toLowerCase().startsWith("x-") || key.toLowerCase().contains("link")) {
                filteredHeaders.put(key, values);
            }
        });
        // "x-"を含むレスポンスヘッダーで抽出する必要のない（"excluded-headers.txt" に記載のある文字列を含む）ヘッダーを削除
        excludedHeaders.forEach(excludedHeader -> {
            filteredHeaders.keySet().removeIf(key -> key.toLowerCase().contains(excludedHeader.toLowerCase()));
        });
        return new ResponseEntity<>(filteredHeaders, statusCode);
    }

    /**
     * 指定されたデータモデルに対してPOSTリクエストを送信します。
     * デフォルトではExceptionをスローするため、POSTを利用するAPIは実装クラスでオーバーライドしてください。
     *
     * @param dataModel 送信するデータモデル
     * @param tracking トラッキングID
     * @return サーバーからのレスポンス
     */
    public ResponseEntity<Object> sendPOST(ApiContext apiContext){
        String pathName = apiContext.apiUrl();
        UUID tracking = apiContext.xTracking();
        Object dataModel = apiContext.datamodel();
        String queryParam = apiContext.queryParam();
        String token = apiContext.token();
        log.info("Send POST to {} service. Tracking = {}", pathName, tracking);
        MultiValueMap<String, String> xheaders = getFilteredHeaders();
        ResponseEntity<Object> result;
        if(queryParam == null) {
            String url = baseUrl + pathName;
            result = this.restClient.build().post()
            .uri(url)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toEntity(Object.class);
        } else if(queryParam.contains("[") && queryParam.contains("]")){
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build().toUri();
            result = this.restClient.build().post()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toEntity(Object.class);
        } else {
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build(true).toUri();
            result = this.restClient.build().post()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toEntity(Object.class);
        }
        HttpStatusCode statusCode = result.getStatusCode();
        Object body = result.getBody();
        // "x-" と "link" を含むレスポンスヘッダーを抽出
        HttpHeaders filteredHeaders = new HttpHeaders();
        result.getHeaders().forEach((key, values) -> {
            if (key.toLowerCase().startsWith("x-") || key.toLowerCase().contains("link")) {
                filteredHeaders.put(key, values);
            }
        });
        // "x-"を含むレスポンスヘッダーで抽出する必要のない（"excluded-headers.txt" に記載のある文字列を含む）ヘッダーを削除
        excludedHeaders.forEach(excludedHeader -> {
            filteredHeaders.keySet().removeIf(key -> key.toLowerCase().contains(excludedHeader.toLowerCase()));
        });
        return new ResponseEntity<>(body , filteredHeaders, statusCode);
    }
    
    /**
     * 指定されたデータモデルに対してPOST(Void型）リクエストを送信します。
     * デフォルトではExceptionをスローするため、POSTを利用するAPIは実装クラスでオーバーライドしてください。
     *
     * @param dataModel 送信するデータモデル
     * @param tracking トラッキングID
     * @return サーバーからのレスポンス
     */
    public ResponseEntity<Void> sendPOST_Void(ApiContext apiContext){
        String pathName = apiContext.apiUrl();
        UUID tracking = apiContext.xTracking();
        Object dataModel = apiContext.datamodel();
        String queryParam = apiContext.queryParam();
        String token = apiContext.token();
        log.info("Send POST to {} service. Tracking = {}", pathName, tracking);
        MultiValueMap<String, String> xheaders = getFilteredHeaders();
        ResponseEntity<Void> result;
        if(queryParam == null) {
            String url = baseUrl + pathName;
            result = this.restClient.build().post()
            .uri(url)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toBodilessEntity();
        } else if(queryParam.contains("[") && queryParam.contains("]")){
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build().toUri();
            result = this.restClient.build().post()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toBodilessEntity();
        } else {
            String url = baseUrl + pathName;
            URI uri = UriComponentsBuilder.fromUriString(url)
            .query(queryParam)
            .build(true).toUri();
            result = this.restClient.build().post()
            .uri(uri)
            .headers(httpHeaders -> {
                httpHeaders.add("Content-Type", "application/json");
                httpHeaders.add("Authorization", token);
                httpHeaders.add("apiKey", apiKey);
                xheaders.forEach((key, values) -> values.forEach(value -> httpHeaders.add(key, value)));
            })
            .body(dataModel)
            .retrieve()
            .toBodilessEntity();
        }
        HttpStatusCode statusCode = result.getStatusCode();
        
        // "x-" と "link" を含むレスポンスヘッダーを抽出
        HttpHeaders filteredHeaders = new HttpHeaders();
        result.getHeaders().forEach((key, values) -> {
            if (key.toLowerCase().startsWith("x-") || key.toLowerCase().contains("link")) {
                filteredHeaders.put(key, values);
            }
        });
        // "x-"を含むレスポンスヘッダーで抽出する必要のない（"excluded-headers.txt" に記載のある文字列を含む）ヘッダーを削除
        excludedHeaders.forEach(excludedHeader -> {
            filteredHeaders.keySet().removeIf(key -> key.toLowerCase().contains(excludedHeader.toLowerCase()));
        });

        return new ResponseEntity<>(filteredHeaders, statusCode);
    }
}
