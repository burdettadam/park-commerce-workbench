# G-0004-A02: retained dependency inputs

Source S: `e7c1e7f309bd976b6bb92e575374c1bb1add316d`.

| Input | Identity and strategy | Observation |
| --- | --- | --- |
| `storefront/package.json`, `storefront/package-lock.json` | Angular 21.2.25, build/CLI 21.2.26, TypeScript 5.9.3; lock SHA-256 `a377d33b2e4a26b0678cd90320e1d3fbc5f91f47a87c7502ae6f57da229b7b65`. | Fresh `npm ci` installed 406 packages and produced the production bundle; tracked lock remained unchanged. A separate earlier clean detached checkout repeated this successfully. No Angular dependency correction was needed. |
| `admin-console/package.json`, `admin-console/package-lock.json` | Vaadin 25.3.0-generated manifest retained under source control; lock SHA-256 `89f7c27640fedb619d14a4de30b8c4daa50d729c2650131a034d1ad01a37ee53`. `pom.xml` production profile sets `forceProductionBuild=true` and `ciBuild=true`. | Hosted S build ran actual `npm ci` and Vite; CI `git diff --exit-code` confirmed no frontend manifest/lock change. A second fresh detached S checkout with empty `node_modules` and a separate Maven volume ran the same command; see [second clean rebuild](checks/clean-vaadin.txt). |
| Root and module Maven POMs | Spring Boot parent 4.1.1, Java release 21, Vaadin BOM/plugin 25.3.0. Maven transitive coordinates are recorded in [dependency tree](checks/dependency-tree.txt). | Hosted integration and optional UI Maven builds passed. Maven coordinates are versioned by the POMs/BOM; Maven has no npm-style lockfile in this repo. |

The generated Vaadin npm manifest has `name: no-name` and `license: UNLICENSED` because it is a private build input, not a separately published package. The repository license is MIT. Vaadin's [source-control guidance](https://vaadin.com/docs/latest/flow/configuration/source-control) supports retaining these inputs, and its [production build guidance](https://vaadin.com/docs/latest/flow/production/production-build) documents the two profile options.

The full CI command outputs are linked from [A01](build-results.md); failed exploratory attempts and their fixes remain in the [investigation](../../work/build-investigation.md). The separate clean S rebuild is required before a final AC02 pass claim.
