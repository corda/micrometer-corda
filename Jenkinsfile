#! groovy
import groovy.transform.Field

@Library('corda-shared-build-pipeline-steps@5.0.1') _

@Field
String mavenLocal = 'tmp/mavenlocal'

pipeline{
    agent {
        docker {
            image 'build-zulu-openjdk:11'
            label 'standard'
            registryUrl 'https://engineering-docker.software.r3.com/'
            registryCredentialsId 'artifactory-credentials'
        }
    }

    // purposely excluding artifactory access as we are replicating a non r3 user, setting grade home in work space so we can delete it each run
    environment {
        GRADLE_USER_HOME = "${WORKSPACE}"
        GRADLE_PERFORMANCE_TUNING = "--max-workers=4 --parallel "
    }

    options {
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: "14", numToKeepStr: ''))
        timestamps()
    }

    stages {
        stage('Prep') {
            steps { // remove any cached items if we end up on same VM
                echo "placeholder"
            }
        }

        stage('Build') {
            steps {
                sh './gradlew assemble'
            }
        }
    }
}
