#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
UI_DIR="$ROOT_DIR/ui"
STATIC_DIR="$ROOT_DIR/src/main/resources/static"

echo "==> Building VCDP single-user tool"

command -v npm >/dev/null 2>&1 || {
  echo "ERROR: npm is required but was not found." >&2
  exit 1
}

command -v mvn >/dev/null 2>&1 || {
  echo "ERROR: Maven is required but was not found." >&2
  exit 1
}

echo "==> Installing frontend dependencies"
cd "$UI_DIR"
npm install --no-package-lock

echo "==> Building frontend into Spring Boot static resources"
npm run build

if [[ ! -f "$STATIC_DIR/index.html" ]]; then
  echo "ERROR: Frontend build did not produce $STATIC_DIR/index.html" >&2
  exit 1
fi

echo "==> Packaging Spring Boot jar"
cd "$ROOT_DIR"
mvn clean package

JAR_PATH="$(find "$ROOT_DIR/target" -maxdepth 1 -type f -name '*.jar' ! -name '*sources.jar' | head -n 1)"
if [[ -z "$JAR_PATH" ]]; then
  echo "ERROR: No jar package was produced under $ROOT_DIR/target" >&2
  exit 1
fi

echo "==> Build completed"
echo "Jar: $JAR_PATH"
echo "Run: java -jar \"$JAR_PATH\""
