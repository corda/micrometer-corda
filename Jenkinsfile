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
        CORDA_PUBLISH_REPOSITORY_KEY = "corda-dependecies-dev"
        CORDA_ARTIFACTORY_PASSWORD = "${env.ARTIFACTORY_CREDENTIALS_PSW}"
        CORDA_ARTIFACTORY_USERNAME = "${env.ARTIFACTORY_CREDENTIALS_USR}"
        ARTIFACTORY_CREDENTIALS = credentials('artifactory-credentials')
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

        stage('publish') {
            steps {
                sh './gradlew artifactoryPublish'
            }
        }
    }
}
