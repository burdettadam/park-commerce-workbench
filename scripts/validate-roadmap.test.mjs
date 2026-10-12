import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import { validateRoadmap, projectRoot, safeRelative } from './validate-roadmap.mjs';
const original = JSON.parse(fs.readFileSync(path.join(projectRoot,'docs/roadmap/catalog.json'),'utf8'));
function rejects(title, mutate, pattern) {
  test(title, () => { const d = structuredClone(original); mutate(d); const result=validateRoadmap(projectRoot,d); assert.ok(result.errors.some(x=>pattern.test(x)), JSON.stringify(result.errors)); });
}
test('valid catalog resolves every milestone, goal, criterion and artifact contract',()=>{
  const r=validateRoadmap(); assert.deepEqual(r.errors,[]); assert.equal(r.milestones,10); assert.equal(r.goals,30); assert.equal(r.criteria,120); assert.equal(r.artifacts,90);
});
rejects('duplicate goal IDs are rejected',d=>d.goals.push(structuredClone(d.goals[0])),/Duplicate goal/);
rejects('unknown dependency is rejected',d=>d.goals[1].depends_on.push('G-9999'),/unknown dependency/);
rejects('dependency cycles are rejected',d=>d.goals[0].depends_on.push('G-0002'),/Dependency cycle/);
rejects('criterion with no matching artifact is rejected',d=>d.goals[0].criteria[0].artifact_id='MISSING',/unknown artifact/);
rejects('missing goal file is rejected',d=>d.goals[0].path='docs/roadmap/missing.md',/Missing\/unsafe planning file/);
rejects('criterion text drifting from its proposal is rejected',d=>d.goals[0].criteria[0].text='Lower the criteria after failure.',/criterion text\/ID drift/);
rejects('empty acceptance contract is rejected',d=>d.goals[0].criteria=[],/empty acceptance\/artifact contract/);
rejects('unsafe future artifact path is rejected',d=>d.goals[0].artifacts[0].path='../escape.txt',/unsafe\/duplicate artifact path/);
rejects('unknown claimed command is rejected',d=>d.goals[0].commands.push('INVENTED_RUNNER'),/unknown command/);
rejects('milestone membership drift is rejected',d=>d.milestones[0].goal_ids.pop(),/milestone membership mismatch/);
rejects('coverage table drift is rejected',d=>d.requirement_coverage['TOOL-01']=[],/Requirement coverage drift/);
rejects('uncovered original requirement is rejected',d=>{ for(const g of d.goals) g.requirements=g.requirements.filter(x=>x!=='OBS-01'); delete d.requirement_coverage['OBS-01']; },/Existing requirement has no goal coverage/);
rejects('duplicate artifact output paths are rejected',d=>d.goals[0].artifacts[1].path=d.goals[0].artifacts[0].path,/unsafe\/duplicate artifact path/);
test('path validation refuses traversal, Windows paths, backslashes and controls',()=>{
  for(const p of ['../escape','/absolute','C:/escape','a\\b','a/../b','a//b','a/./b','a\n']) assert.equal(safeRelative(p),false,p);
  assert.equal(safeRelative('reviews/contract.md'),true);
});
