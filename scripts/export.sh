#!/usr/bin/env bash
# Static export to site/.kobweb/site. Uses the installed Chrome for snapshots so Playwright skips its browser download.
set -euo pipefail
cd "$(dirname "$0")/.."

CHROME="${KOBWEB_EXPORT_BROWSER_PATH:-/Applications/Google Chrome.app/Contents/MacOS/Google Chrome}"

./gradlew :site:kobwebExport \
  -PkobwebReuseServer=false -PkobwebEnv=DEV -PkobwebRunLayout=FULLSTACK \
  -PkobwebBuildTarget=RELEASE -PkobwebExportLayout=STATIC \
  "-Pkobweb.export.browserPath=$CHROME"
# Export boots a temporary Kobweb server; don't leave it running.
./gradlew :site:kobwebStop
