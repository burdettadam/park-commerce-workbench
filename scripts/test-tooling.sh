#!/usr/bin/env bash
# Offline tests of this project's wrappers, NOT of the private git-agent initializer.
set -euo pipefail
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
WORK=$(mktemp -d "${TMPDIR:-/tmp}/park tooling.XXXXXX")
trap 'rm -rf "$WORK"' EXIT
passes=0
pass() { passes=$((passes+1)); printf 'PASS %s\n' "$1"; }
fail() { printf 'FAIL %s\n' "$1" >&2; exit 1; }
mkdir -p "$WORK/project/scripts"
cp "$ROOT/scripts/configure-local.sh" "$ROOT/scripts/init-agent.sh" "$WORK/project/scripts/"
bash "$WORK/project/scripts/configure-local.sh" > "$WORK/output"
[[ -f "$WORK/project/.env" ]] || fail 'No .env'
pass 'Fresh local configuration is generated'
count=0; values=()
while IFS='=' read -r name value; do
 case "$name" in *_TOKEN|*_PASSWORD)
  [[ $value =~ ^[0-9a-f]{48}$ ]] || fail 'Credential is not 24 random bytes in hexadecimal'
  for old in "${values[@]:-}"; do [[ $value != "$old" ]] || fail 'Repeated credential'; done
  values+=("$value"); count=$((count+1))
  if grep -F "$value" "$WORK/output" >/dev/null; then fail 'Secret printed'; fi
 ;; esac
done < "$WORK/project/.env"
[[ $count == 9 ]] || fail 'Expected nine credentials'
pass 'Nine distinct random credentials are generated without printing them'
case "$(uname -s)" in
  MINGW*|MSYS*|CYGWIN*)
    win_path=$(cygpath -w "$WORK/project/.env")
    identity=$(whoami.exe | tr -d '\r')
    acl=$(MSYS_NO_PATHCONV=1 icacls.exe "$win_path" | tr -d '\r')
    [[ $acl == *"${identity}:(F)"* && $acl != *'(I)'* ]] || fail 'Expected a private Windows ACL on .env'
    [[ $(printf '%s\n' "$acl" | grep -Ec ':\([^)]+\)') == 1 ]] || fail 'Unexpected additional Windows ACL entry on .env'
    pass 'Generated local credentials have a private Windows ACL'
    ;;
  *)
    if [[ $(uname -s) == Darwin ]]; then mode=$(stat -f '%Lp' "$WORK/project/.env"); else mode=$(stat -c '%a' "$WORK/project/.env"); fi
    [[ $mode == 600 ]] || fail 'Expected restrictive .env permissions'
    pass 'Generated local credentials have mode 600'
    ;;
esac
cp "$WORK/project/.env" "$WORK/original"
if bash "$WORK/project/scripts/configure-local.sh" > "$WORK/output" 2>&1; then fail 'Overwrote existing configuration'; fi
cmp -s "$WORK/project/.env" "$WORK/original" || fail 'Configuration changed'
pass 'Existing local configuration is preserved'
# Missing local Git administration must not initialize the parent unexpectedly.
if bash "$WORK/project/scripts/init-agent.sh" --preview > "$WORK/output" 2>&1; then fail 'Accepted a non-repository'; fi
pass 'Agent wrapper refuses non-repository targets'
git -C "$WORK" init -q -b main
if bash "$WORK/project/scripts/init-agent.sh" --preview > "$WORK/output" 2>&1; then fail 'Accepted parent worktree'; fi
pass 'Agent wrapper refuses to initialize a parent worktree'
git -C "$WORK/project" init -q -b main
mkdir "$WORK/bin"
cat > "$WORK/bin/git-agent" <<'MOCK'
#!/usr/bin/env bash
if [[ ${1:-} == --version ]]; then
 if [[ ${MOCK_OUTPUT+x} ]]; then printf '%s' "$MOCK_OUTPUT"; else printf 'git-agent %s\n' "${MOCK_VERSION:-0.2.0}"; fi
 exit "${MOCK_VERSION_EXIT:-0}"
fi
printf '%s\n' "$@" > "$MOCK_CAPTURE"
exit "${MOCK_EXIT:-0}"
MOCK
chmod +x "$WORK/bin/git-agent"
export PATH="$WORK/bin:$PATH" MOCK_CAPTURE="$WORK/args"
bash "$WORK/project/scripts/init-agent.sh" --preview
[[ $(wc -l < "$WORK/args") == 3 && $(sed -n '1p' "$WORK/args") == init && $(sed -n '2p' "$WORK/args") -ef "$WORK/project" && $(sed -n '3p' "$WORK/args") == --dry-run ]] || fail 'Preview arguments incorrect'
pass 'Preview forwards the project root and dry-run to a mock initializer'
bash "$WORK/project/scripts/init-agent.sh" --apply
[[ $(wc -l < "$WORK/args") == 2 && $(sed -n '1p' "$WORK/args") == init && $(sed -n '2p' "$WORK/args") -ef "$WORK/project" ]] || fail 'Apply arguments incorrect'
pass 'Apply forwards only the project root to a mock initializer'
if MOCK_VERSION=9.0.0 bash "$WORK/project/scripts/init-agent.sh" --preview > "$WORK/output" 2>&1; then fail 'Accepted unreviewed version'; fi
pass 'Unreviewed initializer versions are rejected'
status=0; MOCK_EXIT=3 bash "$WORK/project/scripts/init-agent.sh" --apply || status=$?
[[ $status == 3 ]] || fail 'Did not retain adoption proposal status'
pass 'Initializer proposal exit status is preserved'
if bash "$WORK/project/scripts/init-agent.sh" --apply unexpected > "$WORK/output" 2>&1; then fail 'Ignored extra argument'; fi
pass 'Extra initializer-wrapper arguments are rejected'
# Fail closed before initialization, even when a rejected output contains the pin.
# Aggregate failures so the red run records each known look-alike independently.
version_failures=0
reject_version() {
 local label=$1 output=$2 status=0
 rm -f "$MOCK_CAPTURE"
 MOCK_OUTPUT="$output" bash "$WORK/project/scripts/init-agent.sh" --preview > "$WORK/output" 2>&1 || status=$?
 if [[ $status == 2 && ! -e $MOCK_CAPTURE ]]; then
  pass "Reject $label before calling init"
 else
  printf 'FAIL reject %s: exit=%s init_called=%s\n' "$label" "$status" "$([[ -e $MOCK_CAPTURE ]] && printf yes || printf no)"
  version_failures=$((version_failures+1))
 fi
}
reject_version 'patch look-alike 0.2.01' 'git-agent 0.2.01'
reject_version 'major look-alike 10.2.0' 'git-agent 10.2.0'
reject_version 'prerelease 0.2.0-rc.1' 'git-agent 0.2.0-rc.1'
reject_version 'build suffix' 'git-agent 0.2.0+unreviewed'
reject_version 'empty output' ''
reject_version 'missing command name' '0.2.0'
reject_version 'wrong command name' 'other-agent 0.2.0'
reject_version 'leading space' ' git-agent 0.2.0'
reject_version 'trailing space' 'git-agent 0.2.0 '
reject_version 'banner before version' $'notice\ngit-agent 0.2.0'
reject_version 'banner after version' $'git-agent 0.2.0\nnotice'
reject_version 'carriage return' $'git-agent 0.2.0\r'
rm -f "$MOCK_CAPTURE"
status=0
MOCK_OUTPUT='git-agent 0.2.0' MOCK_VERSION_EXIT=9 bash "$WORK/project/scripts/init-agent.sh" --preview > "$WORK/output" 2>&1 || status=$?
[[ $status == 2 && ! -e $MOCK_CAPTURE ]] || fail 'Version probe failure reached init'
pass 'An unsuccessful version probe fails before init even with correct output'
for expected in 2 3 130 143; do
 status=0; MOCK_EXIT=$expected bash "$WORK/project/scripts/init-agent.sh" --apply > "$WORK/output" 2>&1 || status=$?
 [[ $status == "$expected" ]] || fail "Initializer exit $expected not preserved"
 pass "Initializer exit $expected is preserved"
done
cmp -s "$WORK/project/.env" "$WORK/original" || fail 'Agent wrapper changed configuration'
pass 'Agent checks preserve existing local configuration bytes'
(( version_failures == 0 )) || { printf 'RESULT: %s rejected-output regressions failed.\n' "$version_failures"; exit 1; }
printf 'RESULT: %s tooling checks passed. Initializer calls used a mock, not git-agent.\n' "$passes"
