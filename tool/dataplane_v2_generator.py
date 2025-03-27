import os
import argparse
import subprocess
import shutil

# OpenAPI Generatorコマンドの実行
def generate_spring_project(openapi_yaml_path, output_dir, template_path):
    command = [
        "openapi-generator-cli", "generate",
        "-i", openapi_yaml_path,
        "-g", "spring",
        "-o", output_dir,
        "-t", template_path,
        "--additional-properties", "useSpringBoot3=true", # import文をjavaxからjakarta仕様に変更
        "--model-package", "net.ouranos.domain.model", # モデルのパッケージを指定
        "--api-package", "net.ouranos.application.controller" # apiのパッケージを指定
    ]
    result = subprocess.run(command, capture_output=True, text=True, shell=True)
    if result.returncode == 0:
        print("Springプロジェクトの生成完了")
    else:
        print(f"Springプロジェクトの生成に失敗: {result.stderr}")
        exit(1)

# 生成されたコントローラのインターフェースを指定ディレクトリに移動（過渡的）
def move_controller_interfaces(source_dir, target_dir):
    if not os.path.exists(target_dir):
        os.makedirs(target_dir)

    for filename in os.listdir(source_dir):
        if "Api.java" in filename or "ApiController.java" in filename:
            shutil.copy(os.path.join(source_dir, filename), target_dir)
            print(f"Moved {filename} to {target_dir}")

# メイン処理
def main(openapi_yaml_path, output_dir, template_path, target_directory):
    generate_spring_project(openapi_yaml_path, output_dir, template_path)
    source_dir = os.path.join(output_dir, "src\\main\\java\\net\\ouranos\\application\\controller")
    move_controller_interfaces(source_dir, target_directory)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="OpenAPI Generatorとファイル移動スクリプト")
    parser.add_argument("-i", "--input", required=True, help="OpenAPI YAMLファイルのパス")
    parser.add_argument("-o", "--output", required=True, help="生成されたSpringプロジェクトの出力ディレクトリ")
    parser.add_argument("-t", "--template", required=True, help="OpenAPI Generatorのカスタムテンプレートのディレクトリ")
    parser.add_argument("-g", "--generate", required=True, help="コントローラのインターフェースが生成されるディレクトリ")
    args = parser.parse_args()
    main(args.input, args.output, args.template, args.generate)