#!/usr/bin/env node
// Read-only project planning checks. Does not launch agents or change goal state.
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';

export const projectRoot = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
export function safeRelative(value) {
  return typeof value === 'string' && value.length > 0 && !/[\\\x00-\x1f\x7f]/.test(value)
    && !path.posix.isAbsolute(value) && !/^[A-Za-z]:/.test(value)
    && value.split('/').every(x => x.length > 0 && x !== '.' && x !== '..');
}
export function ownedFile(root, relative) {
  if (!safeRelative(relative)) throw new Error(`Unsafe relative path: ${relative}`);
  let current = path.resolve(root);
  for (const component of relative.split('/')) {
    current = path.join(current, component);
    if (fs.lstatSync(current).isSymbolicLink()) throw new Error(`Symlink refused: ${relative}`);
  }
  if (!fs.statSync(current).isFile()) throw new Error(`Not a regular file: ${relative}`);
  return current;
}
export function validateRoadmap(root = projectRoot, supplied = null) {
  const errors = [];
  const error = s => errors.push(s);
  let data = supplied;
  try { data ??= JSON.parse(fs.readFileSync(ownedFile(root, 'docs/roadmap/catalog.json'), 'utf8')); }
  catch (e) { return { errors: [`Cannot read catalog: ${e.message}`], goals: 0, milestones: 0, criteria: 0, artifacts: 0 }; }
  if (!data || data.schema_version !== 1) error('Unsupported roadmap schema_version.');
  const goals = Array.isArray(data?.goals) ? data.goals : [];
  const milestones = Array.isArray(data?.milestones) ? data.milestones : [];
  const commands = Array.isArray(data?.commands) ? data.commands : [];
  if (!goals.length || !milestones.length) error('Roadmap must contain nonempty goals and milestones.');
  const goalMap = new Map(), milestoneMap = new Map(), commandMap = new Map();
  const register = (items, map, re, kind) => {
    for (const x of items) {
      if (!x || typeof x !== 'object' || !re.test(x.id)) { error(`Invalid ${kind} ID.`); continue; }
      if (map.has(x.id)) error(`Duplicate ${kind} ID: ${x.id}`);
      map.set(x.id, x);
    }
  };
  register(goals, goalMap, /^G-\d{4}$/, 'goal');
  register(milestones, milestoneMap, /^M\d+$/, 'milestone');
  register(commands, commandMap, /^[A-Z][A-Z0-9_]+$/, 'command');
  const read = p => { try { return fs.readFileSync(ownedFile(root, p), 'utf8'); } catch(e) { error(`Missing/unsafe planning file ${p}: ${e.message}`); return ''; } };
  const coverage = new Map();
  let criteriaCount = 0, artifactCount = 0;
  for (const g of goals) {
    if (!g || !goalMap.has(g.id)) continue;
    const card = read(g.path);
    if (!card.includes(`# ${g.id} — ${g.title}`)) error(`${g.id}: card title differs from catalog.`);
    if (!milestoneMap.has(g.milestone)) error(`${g.id}: unknown milestone ${g.milestone}`);
    if (!Array.isArray(g.depends_on)) { error(`${g.id}: missing dependency list.`); continue; }
    if (new Set(g.depends_on).size !== g.depends_on.length) error(`${g.id}: duplicate dependency.`);
    for (const d of g.depends_on) if (!goalMap.has(d)) error(`${g.id}: unknown dependency ${d}`);
    if (!card.includes(`Depends on: ${g.depends_on.join(', ') || 'none'}\n`)) error(`${g.id}: dependency metadata drift.`);
    const criteria = Array.isArray(g.criteria) ? g.criteria : [];
    const artifacts = Array.isArray(g.artifacts) ? g.artifacts : [];
    criteriaCount += criteria.length; artifactCount += artifacts.length;
    if (!criteria.length || !artifacts.length) error(`${g.id}: empty acceptance/artifact contract.`);
    const ids = new Set(), artifactIDs = new Set(), artifactPaths = new Set();
    for (const a of artifacts) {
      if (!a || !new RegExp(`^${g.id}-A\\d{2}$`).test(a.id) || artifactIDs.has(a.id)) error(`${g.id}: invalid/duplicate artifact ID.`);
      artifactIDs.add(a?.id);
      if (!safeRelative(a?.path) || artifactPaths.has(a?.path)) error(`${g.id}: unsafe/duplicate artifact path.`);
      artifactPaths.add(a?.path);
      if (!card.includes(`\`${a?.id}\``) || !card.includes(`\`${a?.path}\``)) error(`${g.id}: artifact missing from card.`);
    }
    for (const c of criteria) {
      if (!c || !new RegExp(`^${g.id}-AC\\d{2}$`).test(c.id) || ids.has(c.id)) error(`${g.id}: invalid/duplicate criterion ID.`);
      ids.add(c?.id);
      if (!artifactIDs.has(c?.artifact_id)) error(`${g.id}: criterion references unknown artifact ${c?.artifact_id}`);
      if (!card.includes(`### ${c?.id}\n`) || typeof c?.text !== 'string' || !card.includes(c.text)) error(`${g.id}: criterion text/ID drift.`);
    }
    for (const c of g.commands ?? []) if (!commandMap.has(c)) error(`${g.id}: unknown command ${c}`);
    for (const r of g.requirements ?? []) {
      if (!/^[A-Z][A-Z0-9-]+-\d{2}$/.test(r)) error(`${g.id}: invalid requirement ID ${r}`);
      if (!coverage.has(r)) coverage.set(r, []);
      coverage.get(r).push(g.id);
    }
    try {
      const t = JSON.parse(read(g.manifest_template));
      if (t.goal_id !== g.id || t.claim !== 'not_ready' || t.candidate?.source_commit !== null) error(`${g.id}: template falsely populated/claimed ready.`);
      if (JSON.stringify(t.criteria?.map(x => x.id)) !== JSON.stringify(criteria.map(x => x.id))) error(`${g.id}: manifest template criterion drift.`);
      for (const id of artifactIDs) if (!t.artifacts?.some(a => a.id === id)) error(`${g.id}: manifest missing goal-specific artifact ${id}`);
      if (t.criteria?.some(c => c.result !== 'not_run') || t.reviews?.some(r => r.decision !== 'not_run')) error(`${g.id}: template contains invented outcome.`);
    } catch(e) { error(`${g.id}: invalid manifest template: ${e.message}`); }
  }
  for (const m of milestones) {
    read(m.path);
    const expected = goals.filter(g => g.milestone === m.id).map(g => g.id).sort();
    if (!expected.length || JSON.stringify(expected) !== JSON.stringify([...(m.goal_ids ?? [])].sort())) error(`${m.id}: milestone membership mismatch.`);
  }
  const visited = new Set(), visiting = new Set();
  function visit(id, stack) {
    if (visiting.has(id)) { error(`Dependency cycle: ${[...stack, id].join(' -> ')}`); return; }
    if (visited.has(id) || !goalMap.has(id)) return;
    visiting.add(id);
    for (const d of goalMap.get(id).depends_on ?? []) visit(d, [...stack, id]);
    visiting.delete(id); visited.add(id);
  }
  for (const id of goalMap.keys()) visit(id, []);
  for (const [r, ids] of coverage) if (JSON.stringify(ids) !== JSON.stringify(data.requirement_coverage?.[r])) error(`Requirement coverage drift: ${r}`);
  for (const r of Object.keys(data.requirement_coverage ?? {})) if (!coverage.has(r)) error(`Unknown requirement coverage row: ${r}`);
  const requirements = read('docs/project/requirements.md');
  for (const line of requirements.split('\n')) {
    const m = /^\| ([A-Z][A-Z0-9-]+-\d{2}) \|/.exec(line);
    if (m && !coverage.has(m[1])) error(`Existing requirement has no goal coverage: ${m[1]}`);
  }
  return { errors, goals: goals.length, milestones: milestones.length, criteria: criteriaCount, artifacts: artifactCount };
}
if (process.argv[1] && import.meta.url === pathToFileURL(path.resolve(process.argv[1])).href) {
  if (process.argv.length !== 2) { console.error('Usage: node scripts/validate-roadmap.mjs'); process.exitCode = 2; }
  else {
    const result = validateRoadmap();
    if (result.errors.length) { for (const e of result.errors) console.error(`FAIL ${e}`); process.exitCode = 1; }
    else console.log(`PASS roadmap structure: ${result.milestones} milestones, ${result.goals} goals, ${result.criteria} criteria, ${result.artifacts} goal-specific artifact requirements.\nNo goal execution, review, approval, deployment or recovery is established by this check.`);
  }
}
