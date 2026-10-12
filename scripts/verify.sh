#!/usr/bin/env bash
set -euo pipefail
(( $# <= 1 )) || { echo "Unexpected extra arguments." >&2; exit 2; }
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "$ROOT"
case "${1:---core}" in
  --core) exec bash scripts/test-core.sh --demo ;;
  --java) command -v mvn >/dev/null || { echo 'Install Maven 3.9+ and JDK 21.' >&2; exit 2; }; exec mvn -B -ntp verify ;;
  --integration)
    command -v mvn >/dev/null || { echo 'Install Maven 3.9+.' >&2; exit 2; }
    command -v docker >/dev/null || { echo 'Docker is required; integration tests are NOT skipped.' >&2; exit 2; }
    docker info >/dev/null
    exec mvn -B -ntp -Pintegration verify ;;
  *) echo 'Usage: bash scripts/verify.sh --core|--java|--integration' >&2; exit 2 ;;
esac
