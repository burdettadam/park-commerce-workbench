#!/usr/bin/env bash
set -euo pipefail
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
cd "$ROOT"
[[ ! -e .env && ! -L .env ]] || { echo '.env already exists; it was not changed.' >&2; exit 2; }
command -v od >/dev/null || { echo 'The stock od command is required.' >&2; exit 2; }
secret() { od -An -N24 -tx1 /dev/urandom | tr -d ' \n'; }
umask 077
# Noclobber creation refuses a simultaneous initializer's existing file.
(
  set -o noclobber
  : > .env
)
cleanup() { rm -f -- .env; }
trap cleanup EXIT
case "$(uname -s)" in
  MINGW*|MSYS*|CYGWIN*)
    for tool in cygpath whoami.exe icacls.exe; do
      command -v "$tool" >/dev/null || { echo "Windows ACL tool missing: $tool" >&2; exit 2; }
    done
    win_path=$(cygpath -w .env)
    identity=$(whoami.exe | tr -d '\r')
    [[ -n $identity ]] || { echo 'Cannot identify the Windows account.' >&2; exit 2; }
    MSYS_NO_PATHCONV=1 icacls.exe "$win_path" /grant:r "${identity}:(F)" /inheritance:r >/dev/null || {
      echo 'Could not restrict .env to the current Windows account.' >&2; exit 2;
    }
    ;;
esac
{
  printf 'COMPOSE_PROJECT_NAME=park-wb-%s\n' "$(secret | cut -c1-8)"
  printf '%s\n' 'CATALOG_PORT=8081' 'PRICING_PORT=8082' 'BOOKING_PORT=8083' 'PRICING_MYSQL_PORT=3307' 'BOOKING_MYSQL_PORT=3308' 'MONGO_PORT=27017'
  for name in DEMO_EMPLOYEE_TOKEN DEMO_GUEST_TOKEN DEMO_OTHER_GUEST_TOKEN DEMO_SERVICE_TOKEN MYSQL_ROOT_PASSWORD PRICING_DB_PASSWORD BOOKING_DB_PASSWORD MONGO_ROOT_PASSWORD MONGO_APP_PASSWORD; do
    printf '%s=%s\n' "$name" "$(secret)"
  done
} >> .env
trap - EXIT
echo 'Created .env with random development-only credentials. No credentials were printed.'
echo 'Keep it private. Set unused ports here when running multiple worktrees.'
