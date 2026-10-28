#!/usr/bin/env bash
# Regenerates og.png and apple-touch-icon.png from the HTML templates here, using the installed Chrome.
set -euo pipefail
cd "$(dirname "$0")"
CHROME="${CHROME:-/Applications/Google Chrome.app/Contents/MacOS/Google Chrome}"
OUT=../../site/src/jsMain/resources/public

shoot() { # template size output
  "$CHROME" --headless=new --disable-gpu --hide-scrollbars --force-device-scale-factor=1 \
    --virtual-time-budget=3000 --window-size="$2" --screenshot="$PWD/$3" "file://$PWD/$1" >/dev/null 2>&1
}

shoot og.html 1200,630 "$OUT/og.png"
shoot icon.html 180,180 "$OUT/apple-touch-icon.png"
