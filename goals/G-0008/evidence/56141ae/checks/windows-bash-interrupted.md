# Prior local environment failures retained

The local Compose and Git Bash attempts occurred at preceding source `98b828371ddcc0d2fd88489fa9f95f02f36895ac`, whose product code matches current source S `56141ae40f05704ff0ae30b232ef878e2943b6c6`. The isolated Compose build stopped producing output; the scoped Compose inspection returned Docker engine HTTP 500; a filtered Docker resource query then hung. I interrupted only my CLI processes. Local project cleanup is unverified, and no local six-service pass is claimed. Other projects were not intentionally touched.

At that prior source, process-local JDK 21 `bash scripts/test-core.sh --demo` exited 1 before tests with `Bash/Service/0x8007274c`. Its ignored raw capture has SHA-256 `9813ac0b678d7ca4b9ad5b1e8203ac6ed710f27b5a10141ad4ffb15931ddb464`. No local Bash rerun was attempted at current S. The assigned implementation writer also found the local Docker CLI unresponsive while preparing S and did not claim a local integration pass.

The free public CI run `38478861553` at exact S separately passed 280 core, 68 real database integration, 3 admin helper, and 43 six-service HTTP checks. Its own Compose project removed its three owned volumes and network. The hosted results do not erase the prior local failures.
