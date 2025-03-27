# 各ソフトウェアのインストール手順
チュートリアルを実行するための環境構築に必要なソフトウェアのインストール手順を示す。  

## JDK (Java Development Kit)
### OpenJDKのインストール
公式のOpenJDKの[ダウンロードサイト](https://jdk.java.net/)から自身のOSに合ったファイルをインストールする。  

### 環境変数の設定
環境変数に```JAVA_HOME```を新規追加し、```OpenJDK```のインストールパスを値として設定する。

### version確認
下記コマンドを実行し、versionを確認する。
```
java -version
```

## apache-maven
### apache-mavenのインストール
公式のapache-mavenの[ダウンロードサイト](https://maven.apache.org/download.cgi)からインストールする。

### 環境変数の設定
環境変数に```M2_HOME```を新規追加し、```apache-maven```のインストールパスを値として設定する。

### version確認
下記コマンドを実行し、versionを確認する。
```
mvn -v
```

## python
### pythonのインストール
公式のpythonの[ダウンロードサイト](https://www.python.org/downloads/)から自身のOSに合ったファイルをインストールする。  

pythonのインストールセットアップ画面に表示される```Add python.exe to PATH```にチェックを入れ、インストールを行う。  

### version確認
下記コマンドを実行し、versionを確認する。
```
python --version
```

## openapi-generator
### openapi-generator-cliのインストール
pythonのインストールが完了したら、下記コマンドを実行し、openapi-generator-cliをインストールする。
```
pip install openapi-generator-cli
```

### version確認
下記コマンドを実行し、versionを確認する。
```
openapi-generator-cli --version
```