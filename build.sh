#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
UI_DIR="$ROOT_DIR/ui"
STATIC_DIR="$ROOT_DIR/src/main/resources/static"

usage() {
  echo "用法: $0 <ui|server|all>" >&2
  echo "  ui      仅构建前端，并刷新 src/main/resources/static" >&2
  echo "  server  仅打包后端" >&2
  echo "  all     前端、后端都打包" >&2
  exit 1
}

require_cmd() {
  local cmd="$1"
  command -v "$cmd" >/dev/null 2>&1 || {
    echo "ERROR: $cmd is required but was not found." >&2
    exit 1
  }
}

refresh_static_dir() {
  mkdir -p "$STATIC_DIR"
  find "$STATIC_DIR" -mindepth 1 -maxdepth 1 -exec rm -rf {} +
}

build_ui() {
  require_cmd npm

  echo "==> Refreshing Spring Boot static resources"
  refresh_static_dir

  echo "==> Installing frontend dependencies"
  cd "$UI_DIR"
  npm install --no-package-lock

  echo "==> Building frontend into Spring Boot static resources"
  npm run build

  if [[ ! -f "$STATIC_DIR/index.html" ]]; then
    echo "ERROR: Frontend build did not produce $STATIC_DIR/index.html" >&2
    exit 1
  fi
}

build_server() {
  require_cmd mvn

  echo "==> Packaging Spring Boot jar"
  cd "$ROOT_DIR"
  mvn clean package

  JAR_PATH="$(find "$ROOT_DIR/target" -maxdepth 1 -type f -name '*.jar' ! -name '*sources.jar' | head -n 1)"
  if [[ -z "$JAR_PATH" ]]; then
    echo "ERROR: No jar package was produced under $ROOT_DIR/target" >&2
    exit 1
  fi

  echo "Jar: $JAR_PATH"
  echo "Run: java -jar \"$JAR_PATH\""
}

if [[ $# -ne 1 ]]; then
  echo "ERROR: 必须传入一个参数。" >&2
  usage
fi

TARGET="$(printf '%s' "$1" | tr '[:upper:]' '[:lower:]')"

case "$TARGET" in
  ui)
    echo "==> Building VCDP frontend"
    build_ui
    echo "==> Frontend build completed"
    echo "Static: $STATIC_DIR"
    ;;
  server)
    echo "==> Building VCDP backend"
    build_server
    echo "==> Backend build completed"
    ;;
  all)
    echo "==> Building VCDP frontend and backend"
    build_ui
    build_server
    echo "==> Build completed"
    ;;
  *)
    echo "ERROR: 未知参数 '$1'。" >&2
    usage
    ;;
esac
