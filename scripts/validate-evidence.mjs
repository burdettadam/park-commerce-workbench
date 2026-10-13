#!/usr/bin/env node
// Read-only evidence-contract validation, never an execution or approval engine.
import fs from 'node:fs';
import path from 'node:path';
import crypto from 'node:crypto';
import { pathToFileURL } from 'node:url';
import { projectRoot, ownedFile, safeRelative } from './validate-roadmap.mjs';

const gitHash = x => typeof x === 'string' && /^(?:[a-f0-9]{40}|[a-f0-9]{64})$/.test(x);
const sha256 = x => typeof x === 'string' && /^[a-f0-9]{64}$/.test(x);
const filled = x => typeof x === 'string' && x.trim().length > 0 && !/^(?:TBD|TODO|UNRECORDED|UNKNOWN|PLACEHOLDER)$/i.test(x.trim());
const count = x => Number.isInteger(x) && x >= 0;
const unique = xs => new Set(xs).size === xs.length;
export function validateEvidence(filename, root = projectRoot) {
  const errors = []; const fail = m => errors.push(m);
  let x, catalog;
  try {
    // Reject a manifest symlink too; all artifact paths are checked under its directory.
    if (fs.lstatSync(filename).isSymbolicLink()) throw new Error('Manifest symlink refused');
    x = JSON.parse(fs.readFileSync(filename, 'utf8'));
    catalog = JSON.parse(fs.readFileSync(ownedFile(root, 'docs/roadmap/catalog.json'), 'utf8'));
  } catch(e) { return { errors: [`Cannot read evidence/catalog: ${e.message}`] }; }
  if (x?.schema_version !== 1) fail('Unsupported evidence schema_version.');
  const goal = catalog.goals.find(g => g.id === x?.goal_id);
  if (!goal) return { errors: ['Unknown goal_id.'] };
  if (x.claim !== 'ready') fail('Template/incomplete evidence: claim must be ready only after real collection.');
  const s = x.candidate?.source_commit;
  const nc = x.candidate?.negative_control;
  if (nc != null && (!gitHash(nc.commit) || !gitHash(nc.derived_from_commit) || !filled(nc.purpose) || nc.commit === s || nc.commit === x.candidate?.base_commit || nc.derived_from_commit === nc.commit)) fail('Negative-control identity, lineage and purpose are invalid.');
  const redSource = commit => commit === x.candidate?.base_commit || (nc != null && commit === nc.commit);
  if (!gitHash(s) || !gitHash(x.candidate?.base_commit) || !gitHash(x.candidate?.accepted_plan_commit)) fail('Actual source/base/accepted-plan Git identities are required.');
  if (x.candidate?.working_tree_clean !== true) fail('Source candidate is not declared clean.');
  if (!safeRelative(x.candidate?.accepted_plan_path)) fail('Accepted plan path is missing or unsafe.');
  if (!sha256(x.candidate?.accepted_plan_sha256) || !sha256(x.candidate?.configuration_sha256)) fail('Frozen plan/configuration SHA-256 identities required.');
  const artifacts = Array.isArray(x.artifacts) ? x.artifacts : [];
  const map = new Map(); const paths = new Set(); const directory = path.dirname(path.resolve(filename));
  for (const a of artifacts) {
    if (!filled(a?.id) || map.has(a.id)) fail('Missing/duplicate artifact ID.');
    map.set(a?.id, a);
    if (paths.has(a?.path)) fail(`Duplicate artifact path: ${a?.path}`);
    paths.add(a?.path);
    const redBaseline = redSource(a?.candidate_commit) && (x.checks ?? []).some(c => c.type === 'expected_failure' && c.candidate_commit === a.candidate_commit && c.output_artifact_id === a.id);
    if (a?.candidate_commit !== s && !redBaseline) fail(`Stale artifact candidate: ${a?.id}`);
    if (!sha256(a?.sha256)) fail(`Missing artifact digest: ${a?.id}`);
    try {
      const file = ownedFile(directory, a?.path);
      const bytes = fs.readFileSync(file);
      if (!bytes.length) fail(`Empty artifact: ${a.id}`);
      const actual = crypto.createHash('sha256').update(bytes).digest('hex');
      if (actual !== a.sha256) fail(`Artifact digest mismatch: ${a.id}`);
    } catch(e) { fail(`Missing/unsafe artifact ${a?.id}: ${e.message}`); }
  }
  const common = ['ENVIRONMENT','PLAN','CONFIGURATION','REVIEW-CONTRACT','REVIEW-ADVERSARIAL','VALIDATION','DECISION','HANDOFF'];
  for (const id of [...goal.artifacts.map(a => a.id), ...common]) if (!map.has(id)) fail(`Missing mandatory artifact: ${id}`);
  if (map.get('PLAN')?.sha256 !== x.candidate?.accepted_plan_sha256) fail('Frozen plan digest mismatch.');
  if (map.get('CONFIGURATION')?.sha256 !== x.candidate?.configuration_sha256) fail('Configuration digest mismatch.');
  if (x.candidate?.environment_artifact_id !== 'ENVIRONMENT') fail('Environment artifact reference required.');
  const checks = Array.isArray(x.checks) ? x.checks : [];
  const checkMap = new Map();
  if (!checks.length) fail('No checks recorded.');
  if (!checks.some(c => c.candidate_commit === s && ['automated','manual'].includes(c.type))) fail('No current-candidate acceptance check recorded.');
  for (const c of checks) {
    if (!filled(c?.id) || checkMap.has(c.id)) fail('Missing/duplicate check ID.');
    checkMap.set(c?.id, c);
    if (c?.candidate_commit !== s && !(c?.type === 'expected_failure' && redSource(c?.candidate_commit))) fail(`Stale check candidate: ${c?.id}`);
    if (!filled(c?.command) || !filled(c?.working_directory) || !filled(c?.observed_result)) fail(`Missing command/procedure/outcome: ${c?.id}`);
    if (c?.status !== 'passed') fail(`Check not passed: ${c?.id}`);
    const start = Date.parse(c?.started_at), end = Date.parse(c?.finished_at);
    if (!Number.isFinite(start) || !Number.isFinite(end) || end < start) fail(`Missing/invalid check timestamps: ${c?.id}`);
    if (!map.has(c?.output_artifact_id)) fail(`Missing check output artifact: ${c?.id}`);
    else if (map.get(c.output_artifact_id).candidate_commit !== c.candidate_commit) fail(`Check output source differs from the observed check: ${c?.id}`);
    if (c?.type === 'automated' || c?.type === 'expected_failure') {
      if (![c.selected,c.passed,c.failed,c.skipped].every(count) || c.selected < 1 || c.selected !== c.passed+c.failed+c.skipped) fail(`Empty/invalid selected check counts: ${c.id}`);
      if (c.skipped !== 0) fail(`Required checks were skipped: ${c.id}`);
      if (!count(c.expected_exit_code) || !count(c.observed_exit_code) || c.expected_exit_code !== c.observed_exit_code) fail(`Unexpected check exit code: ${c.id}`);
      if (c.type === 'automated' && (c.failed !== 0 || c.expected_exit_code !== 0 || c.passed !== c.selected)) fail(`Automated acceptance check failed: ${c.id}`);
      if (c.type === 'expected_failure' && (c.expected_exit_code === 0 || !count(c.failed) || c.failed < 1)) fail(`Expected-failure reproduction is not actually red: ${c.id}`);
    } else if (c?.type === 'manual') {
      if ([c.selected,c.passed,c.failed,c.skipped,c.expected_exit_code,c.observed_exit_code].some(v => v !== null)) fail(`Manual check must not invent automated counts/exit codes: ${c.id}`);
    } else fail(`Unknown check type: ${c?.id}`);
  }
  const criteria = Array.isArray(x.criteria) ? x.criteria : [];
  const expectedIDs = goal.criteria.map(c => c.id).sort();
  if (!unique(criteria.map(c => c.id)) || JSON.stringify(criteria.map(c=>c.id).sort()) !== JSON.stringify(expectedIDs)) fail('Acceptance criterion coverage is missing/duplicated/unknown.');
  for (const c of criteria) {
    const plan = goal.criteria.find(p => p.id === c.id);
    if (c.result !== 'passed' || !filled(c.independent_expected_result)) fail(`Criterion not evidenced: ${c.id}`);
    if (!Array.isArray(c.check_ids) || !c.check_ids.length || c.check_ids.some(id => !checkMap.has(id))) fail(`Criterion check coverage missing: ${c.id}`);
    if (!Array.isArray(c.artifact_ids) || !c.artifact_ids.length || c.artifact_ids.some(id => !map.has(id)) || (plan && !c.artifact_ids.includes(plan.artifact_id))) fail(`Criterion artifact coverage missing: ${c.id}`);
  }
  const reviews = Array.isArray(x.reviews) ? x.reviews : [];
  const reviewException = x.review_policy_exception;
  const fourthRoundAllowed = x.goal_id === 'G-0007' &&
    reviewException?.max_round === 4 &&
    reviewException?.decision_artifact_id === 'REVIEW-EXCEPTION' &&
    map.has('REVIEW-EXCEPTION') &&
    reviewException?.authority_id === 'Adam Burdett';
  if (reviewException != null && !fourthRoundAllowed) fail('Review policy exception is missing, invalid or outside G-0007.');
  if (fourthRoundAllowed && reviews.some(r => r.round !== 4)) fail('G-0007 exception requires both final reviews from round four.');
  if (reviews.length !== 2 || !['contract','adversarial'].every(role => reviews.filter(r => r.role === role).length === 1)) fail('Two independent roles are required: contract and adversarial.');
  if (!unique(reviews.map(r => r.session_id))) fail('Review assignments must have distinct independent sessions.');
  for (const r of reviews) {
    if (!filled(r.reviewer_id) || !filled(r.session_id) || r.independent_of_writer !== true) fail(`Review independence not recorded: ${r.role}`);
    if (r.candidate_commit !== s) fail(`Stale review candidate: ${r.role}`);
    if (!Number.isInteger(r.round) || r.round < 1 || r.round > (fourthRoundAllowed ? 4 : 3)) fail(`Review round outside adopted proposal limit: ${r.role}`);
    if (r.decision !== 'pass' || r.open_blockers !== 0) fail(`Review has open blockers or no pass: ${r.role}`);
    if (r.report_artifact_id !== `REVIEW-${String(r.role).toUpperCase()}` || !map.has(r.report_artifact_id)) fail(`Missing role-specific review report: ${r.role}`);
  }
  const v = x.intended_use_validation;
  if (!filled(v?.validator_id) || v?.candidate_commit !== s || v?.decision !== 'accept' || v?.report_artifact_id !== 'VALIDATION') fail('Actual intended-use validation is missing/stale.');
  const d = x.readiness_decision;
  if (!filled(d?.authority_id) || d?.candidate_commit !== s || d?.decision !== 'ready' || d?.report_artifact_id !== 'DECISION') fail('Actual readiness decision is missing/stale.');
  const retention = x.retention;
  if (!['local_only','private_remote'].includes(retention?.scope) || !filled(retention?.owner_id) || retention?.retrieval_verified !== true || retention?.report_artifact_id !== 'HANDOFF') fail('Retention/retrieval record is incomplete.');
  return { errors, goal_id: goal.id, artifactCount: artifacts.length, checkCount: checks.length };
}
if (process.argv[1] && import.meta.url === pathToFileURL(path.resolve(process.argv[1])).href) {
  if (process.argv.length !== 3) { console.error('Usage: node scripts/validate-evidence.mjs PATH_TO_CANDIDATE_MANIFEST'); process.exitCode=2; }
  else {
    const r = validateEvidence(process.argv[2]);
    if (r.errors.length) { for (const e of r.errors) console.error(`FAIL ${e}`); process.exitCode=1; }
    else console.log(`PASS evidence structure/digests for ${r.goal_id}: ${r.checkCount} declared checks, ${r.artifactCount} local artifacts.\nThis does not authenticate results/reviewers/authority, verify remote Git identity, grant human release approval, or perform any state transition.`);
  }
}
