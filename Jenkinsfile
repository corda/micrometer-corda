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
    }

    options {
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: "14", numToKeepStr: ''))
        timestamps()
    }

    parameters {
        string(name: 'BRANCH_TO_CHECKOUT', defaultValue: 'release/corda-5-beta-1', description: 'Branch of CSDE to check out, defaults to release branch')
    }

    stages {
        stage('Prep') {
            steps { // remove any cached items if we end up on same VM
                echo "placeholder"
            }
        }

        stage('Check out') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: "*/${params.CSDE_BRANCH}"]], extensions: [],
                    userRemoteConfigs: [[credentialsId: 'corda-jenkins-ci02', url: 'https://github.com/corda/micrometer-corda.git']]])
            }
        }

        stage('Build') {
            steps {
                sh './gradlew assemble'
            }
        }
    }
}
