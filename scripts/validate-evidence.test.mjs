// All records and identities below are SYNTHETIC validation fixtures.
// They are not product runs, actual reviewers, actual decisions or Git identities.
import test from 'node:test';
import assert from 'node:assert/strict';
import fs from 'node:fs';
import path from 'node:path';
import os from 'node:os';
import crypto from 'node:crypto';
import { validateEvidence } from './validate-evidence.mjs';
import { projectRoot } from './validate-roadmap.mjs';
const digest=x=>crypto.createHash('sha256').update(x).digest('hex');
const S='a'.repeat(40), B='b'.repeat(40), P='c'.repeat(40), R='d'.repeat(40);
function fixture(goalId='G-0002') {
  const directory=fs.mkdtempSync(path.join(os.tmpdir(),'pcw evidence fixtures '));
  const manifest=JSON.parse(fs.readFileSync(path.join(projectRoot,`docs/roadmap/goals/${goalId}/manifest.template.json`),'utf8'));
  manifest.claim='ready';
  Object.assign(manifest.candidate,{source_commit:S,base_commit:B,accepted_plan_commit:P,working_tree_clean:true});
  for(const a of manifest.artifacts) {
    const file=path.join(directory,a.path); fs.mkdirSync(path.dirname(file),{recursive:true});
    const bytes=Buffer.from(`SYNTHETIC OFFLINE VALIDATOR FIXTURE — ${a.id}\nNot a real product run, review, human approval or retained Git source.\n`);
    fs.writeFileSync(file,bytes); a.sha256=digest(bytes);a.candidate_commit=S;
  }
  const art=id=>manifest.artifacts.find(a=>a.id===id);
  manifest.candidate.accepted_plan_sha256=art('PLAN').sha256;
  manifest.candidate.configuration_sha256=art('CONFIGURATION').sha256;
  Object.assign(manifest.checks[0],{candidate_commit:S,command:'synthetic fixture command — not executed',started_at:'2026-10-11T12:00:00Z',finished_at:'2026-10-11T12:00:01Z',observed_exit_code:0,selected:1,passed:1,failed:0,skipped:0,status:'passed',observed_result:'SYNTHETIC fixture outcome, not an actual test observation.'});
  for(const c of manifest.criteria){c.result='passed';c.check_ids=['CHECK-01'];}
  for(const r of manifest.reviews)Object.assign(r,{reviewer_id:`synthetic-${r.role}`,session_id:`synthetic-${r.role}-session`,independent_of_writer:true,candidate_commit:S,round:1,open_blockers:0,decision:'pass'});
  Object.assign(manifest.intended_use_validation,{validator_id:'synthetic-observer',candidate_commit:S,decision:'accept'});
  Object.assign(manifest.readiness_decision,{authority_id:'synthetic-owner',candidate_commit:S,decision:'ready'});
  Object.assign(manifest.retention,{owner_id:'synthetic-owner',retrieval_verified:true});
  return {directory,manifest,art,save(){const file=path.join(directory,'manifest.json');fs.writeFileSync(file,JSON.stringify(manifest));return file;},clean(){fs.rmSync(directory,{recursive:true,force:true});}};
}
function withFixture(fn){const f=fixture();try{return fn(f);}finally{f.clean();}}
function rejects(title,change,pattern){test(title,()=>withFixture(f=>{change(f);const r=validateEvidence(f.save());assert.ok(r.errors.some(x=>pattern.test(x)),JSON.stringify(r.errors));}));}
test('complete synthetic evidence shape passes without authenticating its claims',()=>withFixture(f=>assert.deepEqual(validateEvidence(f.save()).errors,[])));
test('all 30 unfilled output templates fail readiness as intended',()=>{
  for(let n=1;n<=30;n++){const id=`G-${String(n).padStart(4,'0')}`;const p=path.join(projectRoot,`docs/roadmap/goals/${id}/manifest.template.json`);const r=validateEvidence(p);assert.ok(r.errors.some(x=>/Template\/incomplete/.test(x)),id);}
});
rejects('stale ordinary check cannot satisfy a new candidate',f=>f.manifest.checks[0].candidate_commit=B,/Stale check candidate/);
rejects('stale reviewer coverage is rejected',f=>f.manifest.reviews[0].candidate_commit=B,/Stale review candidate/);
rejects('changed artifact bytes invalidate recorded hash',f=>fs.appendFileSync(path.join(f.directory,f.art('G-0002-A01').path),'changed'),/Artifact digest mismatch/);
rejects('missing artifact output is rejected',f=>fs.unlinkSync(path.join(f.directory,f.art('G-0002-A01').path)),/Missing\/unsafe artifact/);
rejects('dropping a criterion cannot make the goal ready',f=>f.manifest.criteria.pop(),/criterion coverage/);
rejects('omitting a mandatory goal artifact is rejected',f=>f.manifest.artifacts=f.manifest.artifacts.filter(x=>x.id!=='G-0002-A01'),/Missing mandatory artifact/);
rejects('empty selected suite is rejected',f=>Object.assign(f.manifest.checks[0],{selected:0,passed:0}),/Empty\/invalid selected/);
rejects('required skipped tests are rejected',f=>Object.assign(f.manifest.checks[0],{selected:2,skipped:1}),/Required checks were skipped/);
rejects('open valid review blockers prevent structural readiness',f=>f.manifest.reviews[1].open_blockers=1,/open blockers/);
rejects('same reviewer session cannot be counted twice',f=>f.manifest.reviews[1].session_id=f.manifest.reviews[0].session_id,/distinct independent sessions/);
rejects('artifact path traversal is refused',f=>f.art('G-0002-A01').path='../outside.txt',/Missing\/unsafe artifact/);
test('symlinked artifacts are refused',t=>withFixture(f=>{
  const a=f.art('G-0002-A01');
  fs.unlinkSync(path.join(f.directory,a.path));
  try { fs.symlinkSync(path.join(f.directory,f.art('ENVIRONMENT').path),path.join(f.directory,a.path)); }
  catch(e) {
    if (['EPERM','EACCES','ENOTSUP'].includes(e.code)) { t.skip(`Host does not permit symlink creation: ${e.code}`); return; }
    throw e;
  }
  const r=validateEvidence(f.save());
  assert.ok(r.errors.some(x=>/Symlink refused/.test(x)),JSON.stringify(r.errors));
}));
rejects('dirty source is not identified as a frozen candidate',f=>f.manifest.candidate.working_tree_clean=false,/not declared clean/);
rejects('missing intended-use validation is not a pass',f=>f.manifest.intended_use_validation.decision='not_run',/intended-use validation/);
rejects('placeholder Git identity is rejected',f=>f.manifest.candidate.source_commit='NOT_A_COMMIT',/Git identities/);
rejects('configuration snapshot must match the declared digest',f=>f.manifest.candidate.configuration_sha256='d'.repeat(64),/Configuration digest mismatch/);
rejects('round four requires a decision rather than silent acceptance',f=>f.manifest.reviews[0].round=4,/Review round outside/);
test('G-0007 round four accepts only a hashed owner decision artifact',()=>{
  const f=fixture('G-0007');
  try {
    for(const r of f.manifest.reviews)r.round=4;
    const bytes=Buffer.from('SYNTHETIC owner decision fixture; not actual approval.\n');
    fs.writeFileSync(path.join(f.directory,'review-exception.md'),bytes);
    f.manifest.artifacts.push({id:'REVIEW-EXCEPTION',path:'review-exception.md',sha256:digest(bytes),candidate_commit:S});
    f.manifest.review_policy_exception={max_round:4,decision_artifact_id:'REVIEW-EXCEPTION',authority_id:'synthetic-owner'};
    assert.deepEqual(validateEvidence(f.save()).errors,[]);
    fs.appendFileSync(path.join(f.directory,'review-exception.md'),'changed');
    assert.ok(validateEvidence(f.save()).errors.some(x=>/Artifact digest mismatch: REVIEW-EXCEPTION/.test(x)));
  } finally {f.clean();}
});
test('G-0007 round four without the decision artifact is rejected',()=>{
  const f=fixture('G-0007');
  try {f.manifest.reviews[0].round=4;f.manifest.review_policy_exception={max_round:4,decision_artifact_id:'REVIEW-EXCEPTION',authority_id:'synthetic-owner'};
    const errors=validateEvidence(f.save()).errors;
    assert.ok(errors.some(x=>/Review policy exception/.test(x)),JSON.stringify(errors));
    assert.ok(errors.some(x=>/Review round outside/.test(x)),JSON.stringify(errors));
  } finally {f.clean();}
});
rejects('G-0002 cannot borrow the fourth-round exception',f=>{
  f.manifest.reviews[0].round=4;
  f.manifest.review_policy_exception={max_round:4,decision_artifact_id:'REVIEW-EXCEPTION',authority_id:'synthetic-owner'};
},/Review round outside/);
test('a documented G-0007 exception still rejects round five',()=>{
  const f=fixture('G-0007');
  try {
    f.manifest.reviews[0].round=5;
    const bytes=Buffer.from('SYNTHETIC owner decision fixture; not actual approval.\n');
    fs.writeFileSync(path.join(f.directory,'review-exception.md'),bytes);
    f.manifest.artifacts.push({id:'REVIEW-EXCEPTION',path:'review-exception.md',sha256:digest(bytes),candidate_commit:S});
    f.manifest.review_policy_exception={max_round:4,decision_artifact_id:'REVIEW-EXCEPTION',authority_id:'synthetic-owner'};
    assert.ok(validateEvidence(f.save()).errors.some(x=>/Review round outside/.test(x)));
  } finally {f.clean();}
});
rejects('template flag cannot be accepted as ready',f=>f.manifest.claim='not_ready',/Template\/incomplete/);
rejects('missing decision authority is rejected',f=>f.manifest.readiness_decision.authority_id=null,/readiness decision/);
rejects('unretrieved artifact retention is not recoverability',f=>f.manifest.retention.retrieval_verified=false,/Retention\/retrieval/);
rejects('criterion must retain its specified goal artifact',f=>f.manifest.criteria[0].artifact_ids=['ENVIRONMENT'],/Criterion artifact coverage/);
rejects('expected red failure cannot replace all green candidate checks',f=>{Object.assign(f.manifest.checks[0],{type:'expected_failure',expected_exit_code:1,observed_exit_code:1,passed:0,failed:1});},/No current-candidate acceptance check/);
test('a separate pre-fix red run retains its true base source alongside current green',()=>withFixture(f=>{
  const red=structuredClone(f.manifest.checks[0]);Object.assign(red,{id:'RED-BASE',type:'expected_failure',candidate_commit:B,expected_exit_code:1,observed_exit_code:1,passed:0,failed:1,output_artifact_id:'G-0002-A01'});
  f.art('G-0002-A01').candidate_commit=B;f.manifest.checks.push(red);f.manifest.criteria[0].check_ids=['RED-BASE'];assert.deepEqual(validateEvidence(f.save()).errors,[]);
}));
function addNegativeControl(f) {
  f.manifest.candidate.negative_control={commit:R,derived_from_commit:S,purpose:'synthetic deliberately weakened guard'};
  const red=structuredClone(f.manifest.checks[0]);
  Object.assign(red,{id:'RED-CONTROL',type:'expected_failure',candidate_commit:R,expected_exit_code:1,observed_exit_code:1,passed:0,failed:1,output_artifact_id:'G-0002-A01'});
  f.art('G-0002-A01').candidate_commit=R;
  f.manifest.checks.push(red);
  f.manifest.criteria[0].check_ids=['RED-CONTROL'];
}
test('a declared separate negative-control red run retains its source alongside current green',()=>withFixture(f=>{
  addNegativeControl(f);assert.deepEqual(validateEvidence(f.save()).errors,[]);
}));
rejects('undeclared negative-control red check is stale',f=>{addNegativeControl(f);f.manifest.candidate.negative_control=null;},/Stale check candidate/);
rejects('negative-control identity cannot replace source or base',f=>{addNegativeControl(f);f.manifest.candidate.negative_control.commit=S;},/Negative-control identity/);
rejects('negative-control declaration requires a lineage identity',f=>{addNegativeControl(f);f.manifest.candidate.negative_control.derived_from_commit=null;},/Negative-control identity/);
rejects('an ordinary check cannot run against the negative control',f=>{addNegativeControl(f);f.manifest.checks[0].candidate_commit=R;},/Stale check candidate/);
rejects('a negative-control artifact requires its matching red check',f=>{addNegativeControl(f);f.manifest.checks.pop();},/Stale artifact candidate/);
test('manual checks record observations without invented automated test counts',()=>withFixture(f=>{
  Object.assign(f.manifest.checks[0],{type:'manual',selected:null,passed:null,failed:null,skipped:null,expected_exit_code:null,observed_exit_code:null});assert.deepEqual(validateEvidence(f.save()).errors,[]);
}));
