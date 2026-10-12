#!/usr/bin/env bash
set -euo pipefail
(( $# <= 1 )) || { echo "Unexpected extra arguments." >&2; exit 2; }
[[ ${1:-} == "" || ${1:-} == --demo ]] || { echo "Usage: bash scripts/test-core.sh [--demo]" >&2; exit 2; }
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
command -v javac >/dev/null || { echo 'JDK 21 is required.' >&2; exit 2; }
BUILD=$(mktemp -d "${TMPDIR:-/tmp}/park-core.XXXXXX")
trap 'rm -rf "$BUILD"' EXIT
sources=()
while IFS= read -r -d '' file; do sources+=("$file"); done < <(find "$ROOT/core/src/main/java" "$ROOT/core/src/test/java" -name '*.java' ! -name '*Test.java' -print0)
javac -encoding UTF-8 --release 21 -Xlint:all -d "$BUILD" "${sources[@]}"
java -cp "$BUILD" dev.parkcommerce.core.CoreScenarios
if [[ ${1:-} == --demo ]]; then java -cp "$BUILD" dev.parkcommerce.core.CoreDemo; fi
