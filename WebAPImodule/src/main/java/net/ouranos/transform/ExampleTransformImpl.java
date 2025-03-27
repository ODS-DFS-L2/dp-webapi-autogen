package net.ouranos.transform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import net.ouranos.common.context.ApiContext;


@Component
@Qualifier("exampleTransform")   // Qualifierアノテーションを定義し、"<APIのパス名>Transform"という名前を指定する
public class ExampleTransformImpl implements Transform {

    @Override
    public ApiContext encodeDELETE(ApiContext apiContext) {
        return apiContext;
    }

    @Override
    public ResponseEntity<Void> decodeDELETE(ResponseEntity<Void> responseEntity) {
        return responseEntity;
    }

    /*  
    サンプルのTransform実装例。
    この例では、GETメソッドのクエリパラメータのエンコード処理を行う。
    "Id"というString型のクエリパラメータを16進数に変換するという実装例になる。
    */
    @Override
    public ApiContext encodeGET(ApiContext apiContext) {
        // 受け取ったApiContextからクエリパラメータを取得
        String queryParam = apiContext.queryParam();
        if (queryParam != null) {
            // 正規表現を使用してクエリパラメータのIdを取得するためのパターンを作成
            Pattern pattern = Pattern.compile("Id=([^&]*)");
            // パターンを使用してクエリパラメータを検索
            Matcher matcher = pattern.matcher(queryParam);
            if (matcher.find()) {
                // Idの値を取得
                String idValue = matcher.group(1);
                // Idを数値に変換
                int id = Integer.parseInt(idValue);
                // Idを16進数に変換
                String encodedId = Integer.toHexString(id);
                // クエリパラメータを置き換える
                queryParam = queryParam.replace("Id=" + idValue, "Id=" + encodedId);
            }
        }
        // 変換したクエリパラメータを含む新しいApiContextを返す
        return new ApiContext(apiContext.apiUrl(), apiContext.datamodel(), apiContext.xTracking(), queryParam, apiContext.token());
    }

    @Override
    public ResponseEntity<Object> decodeGET(ResponseEntity<Object> responseEntity) {
        return responseEntity;
    }

    @Override
    public ApiContext encodePUT(ApiContext apiContext) {
        return apiContext;
    }

    @Override
    public ResponseEntity<Object> decodePUT(ResponseEntity<Object> responseEntity) {
        return responseEntity;
    }

    @Override
    public ApiContext encodePOST(ApiContext apiContext) {
        return apiContext;
    }

    @Override
    public ResponseEntity<Object> decodePOST(ResponseEntity<Object> responseEntity) {
        return responseEntity;
    }
}
