#!/bin/sh

# 環境変数を設定
export TRUSTSYSTEM_BASEURL="http://$(getent hosts mock-server | awk '{ print $1 }'):4010"

# アプリケーションを起動
exec java -jar /app/WebAPImodule-0.0.1-SNAPSHOT.jar