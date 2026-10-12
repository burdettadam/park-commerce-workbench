#!/usr/bin/env bash
set -euo pipefail
(( $# <= 1 )) || { echo "Unexpected extra arguments." >&2; exit 2; }
ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
mode=${1:---preview}
[[ $mode == --preview || $mode == --apply ]] || { echo 'Usage: bash scripts/init-agent.sh --preview|--apply' >&2; exit 2; }
git -C "$ROOT" rev-parse --is-inside-work-tree >/dev/null 2>&1 || {
  echo 'Use a local clone, or initialize this extracted directory with git init -b main first.' >&2; exit 2;
}
actual=$(git -C "$ROOT" rev-parse --show-toplevel)
[[ "$ROOT" -ef "$actual" ]] || { echo 'Project must be the Git worktree root; refusing initialization of a parent repository.' >&2; exit 2; }
version=$(git -C "$ROOT" agent --version) || { echo 'Install your reviewed git-agent executable first.' >&2; exit 2; }
# Upstream at the pinned revision emits exactly this line. Command substitution
# removes the normal trailing newline; no substring or prerelease match is allowed.
[[ $version == 'git-agent 0.2.0' ]] || { echo 'This handoff was prepared for git-agent 0.2.0. Review compatibility with the installed version first.' >&2; exit 2; }
if [[ $mode == --preview ]]; then exec git -C "$ROOT" agent init "$ROOT" --dry-run; fi
exec git -C "$ROOT" agent init "$ROOT"
