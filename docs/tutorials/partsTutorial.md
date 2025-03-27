# サンプルデータモデル（Parts）のデータ受け渡し
ここでは、サンプルとして格納されている ```Parts``` データモデルを使用したデータ授受の例を示す。  
チュートリアルでは、各HTTPメソッド（GET, PUT, POST, DELETE）のAPIを用いて疎通確認を実施する。  
```<accessToken>```には、アイデンティティコンポーネント（L3）の参照実装であるユーザ認証システム（ic-user-auth）から取得した事業者認証（参考実装）で取得したアクセストークンを使用する。  

## 1. GETメソッド
下記のコマンドを実行し、Partsのデータモデルを取得する。
```shell
$ curl -i -X 'GET' 'http://localhost:8080/parts' \
-H "apiKey:Sample-APIKey1" \
-H "Authorization:Bearer <accessToken>"
```

Partsのデータモデルが返却される。  
```
HTTP/1.1 200
X-Tracking: 497f6eca-6276-4993-bfeb-53cbbbba6f08
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 07 Mar 2025 11:30:55 GMT

{"dataModelType":"test1","attribute":{"dataId":"78aa302c-1600-44b3-a331-e4659c0b28a1","value":123456789}}
```

## 2. PUTメソッド
下記のコマンドを実行し、Partsのデータモデルを更新する。
```shell
$ curl -i -X 'PUT' \
  'http://localhost:8080/parts' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -H "apiKey:Sample-APIKey1" \
  -H "Authorization:Bearer <accessToken>" \
  -d '{
  "dataModelType": "test1",
  "attribute": {
    "dataId": "78aa302c-1600-44b3-a331-e4659c0b28a1",
    "value": 123456789
  }
}'
```
Partsのデータモデルが更新される。  
```
HTTP/1.1 200
X-Tracking: 497f6eca-6276-4993-bfeb-53cbbbba6f08
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 07 Mar 2025 11:33:59 GMT

{"dataModelType":"test1","attribute":{"dataId":"78aa302c-1600-44b3-a331-e4659c0b28a1","value":123456789}}
```

## 3. POSTメソッド
下記のコマンドを実行し、Partsのデータモデルを更新する。
```shell
$ curl -i -X 'POST' \
  'http://localhost:8080/parts' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -H "apiKey:Sample-APIKey1" \
  -H "Authorization:Bearer <accessToken>" \
  -d '{
  "dataModelType": "test1",
  "attribute": {
    "dataId": "78aa302c-1600-44b3-a331-e4659c0b28a1",
    "value": 123456789
  }
}'
```
Partsのデータモデルが更新される。  
```
HTTP/1.1 200
X-Tracking: 497f6eca-6276-4993-bfeb-53cbbbba6f08
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 07 Mar 2025 11:37:49 GMT

{"dataModelType":"test1","attribute":{"dataId":"78aa302c-1600-44b3-a331-e4659c0b28a1","value":123456789}}
```

## 4. DELETEメソッド
下記のコマンドを実行し、Partsのデータモデルを削除する。
```shell
$ curl -i -X 'DELETE' 'http://localhost:8080/parts?id=123456789' \
  -H "apiKey:Sample-APIKey1" \
  -H "Authorization:Bearer <accessToken>"
```
Partsのデータモデルが削除される。（返り値はなし）  
```
HTTP/1.1 200
Content-Length: 0
Date: Fri, 07 Mar 2025 11:39:02 GMT
```



