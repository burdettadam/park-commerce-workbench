// Requires a trusted worker with JDK 21, Maven 3.9+, Node 24, Docker, and registry network access.
// This pipeline verifies candidates. It does not deploy, publish, or tag releases.
pipeline {
  agent any
  options { timestamps(); timeout(time: 30, unit: 'MINUTES'); disableConcurrentBuilds() }
  stages {
    stage('Client recovery and goal artifacts') {
      steps {
        sh 'node --test storefront/src/app/*.test.mjs'
        sh 'node scripts/validate-roadmap.mjs'
        sh 'node --test scripts/validate-roadmap.test.mjs scripts/validate-evidence.test.mjs'
      }
    }
    stage('Core invariants') { steps {
      sh 'bash scripts/test-core.sh'
      sh 'node --test scripts/pricing-contract.test.mjs'
    } }
    stage('Java and real database integration') { steps { sh 'bash scripts/verify.sh --integration' } }
    stage('Container build') {
      steps {
        sh '''for service in catalog-service pricing-service booking-service; do
          docker build --build-arg SERVICE="$service" -t "park-$service:$BUILD_NUMBER" . || exit 1
        done'''
      }
    }
  }
  post { always { junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml,**/target/failsafe-reports/*.xml' } }
}
