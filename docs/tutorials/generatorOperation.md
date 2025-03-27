# generatorによる自動生成手順
OpenAPI仕様書から、WebAPI転送モジュールに必要なファイルをgeneratorで自動生成する手順を示す。

## 1. 前提
### 1.1 自動生成に必要なソフトウェア
以下のソフトウェアをインストールする。
- python
- openapi-generator

動作確認済み実行環境
|Name                                        |Version |
|:-------------------------------------------|:-------|
|python                                    |3.12.4|
|openapi-generator-cli                     |7.6.0|

### 1.2 自動生成される対象について
|コンポーネント |説明 |自動生成対象 |
|:----------------|:-------|:-------|
|Model      |データモデルを定義する|○|
|API interface|Controllerのインターフェースクラス|○|
|Controller |リクエストのハンドリングを行う|○|
|Transform  |送受信したデータを必要な形式に変換する|×|
|Unit Test  |各クラスの単体テストを行う|×|

自動生成の対象となるのは、 ```Model``` , ```API interface``` , ```Controller``` の3つで、```Transform``` , ```Unit Test``` の2つは自動生成の対象外となる。  
```Transform``` を実装する必要がない場合は、自動生成されるファイルのみでデータ授受を実施できる。  

## 2. Generator実行手順
以下、 ```path/to/dp-webapi-autogen``` はローカル環境に ```git clone``` されたWebAPI転送モジュールへのパスを表す。  
generator資材は ```path/to/dp-webapi-autogen/tool``` 配下に入っている。  

下記のコマンドでGeneratorを実行する。
```shell
$ cd <path/to/dp-webapi-autogen/tool>
$ python dataplane_v2_generator.py -i <OpenAPI仕様書のyamlファイルのパス> -o <生成されるSpringプロジェクトの生成先> -t templates/spring/ -g <Controllerファイルの生成先>
```

上記実行後、以下のパスに ```model``` , ```API interface``` , ```Controller``` のファイルが生成される。  

```Model``` ファイル : ```<生成されるSpringプロジェクトの生成先>/src/main/java/net/ouranos/domain/model```  
例: Example.java, DataModelExample.javaなど

```API interface``` ファイル : ```<Controllerファイルの生成先>```  
例: ExampleApi.javaなど

```Controller``` ファイル : ```<Controllerファイルの生成先>```  
例: ExampleApiController.javaなど

## 3. 生成ファイルの格納先について
生成したファイルの格納先をそれぞれ以下に示す。生成したファイルは対象のフォルダに全て格納する。  

```model``` ファイル → ```path/to/dp-webapi-autogen/WebAPImodule/src/main/java/net/ouranos/domain/model```

```API interface``` , ```Controller``` ファイル → ```path/to/dp-webapi-autogen/WebAPImodule/src/main/java/net/ouranos/application/controller```

## 4. application.propertiesへの設定追加
新規にAPIを作成した際は、APIのパス名を[application.properties](../../WebAPImodule/src/main/resources/application.properties)の```#url```に追記する。  
例：APIのパス名が```/example``` の場合、```api.example.url=/example``` を追記する。

## 5. 留意点
以下の2点の留意事項がある。  
- Transformの実装クラスを作成する場合は、```@Qualifierアノテーション``` を使用し、```<APIのパス名>Transform``` と定義する。  
例：APIのパス名が ```/example``` の場合、```@Qualifier("exampleTransform")``` を実装クラスに定義する。

- 各クラスの単体テストについては自動生成されないため、必要に応じて作成する。

参考として、Transformと単体テストのサンプルをそれぞれ以下に格納している。
- [サンプルTransform](../../WebAPImodule/src/main/java/net/ouranos/transform/ExampleTransformImpl.java)
- [サンプル単体テスト](../../WebAPImodule/src/test/java/net/ouranos/application/controller/PartsApiControllerTest.java)