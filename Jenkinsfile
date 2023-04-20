#! groovy
@Library('corda-shared-build-pipeline-steps@5.0.1') _

import groovy.transform.Field
import com.r3.build.utils.GitUtils


@Field
String mavenLocal = 'tmp/mavenlocal'

@Field
GitUtils gitUtils = new GitUtils(this)

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
        CORDA_PUBLISH_REPOSITORY_KEY = "${gitUtils.isReleaseTag() ? 'corda-dependencies-dev' : 'corda-dependencies-dev'}"
        CORDA_ARTIFACTORY_PASSWORD = "${env.ARTIFACTORY_CREDENTIALS_PSW}"
        CORDA_ARTIFACTORY_USERNAME = "${env.ARTIFACTORY_CREDENTIALS_USR}"
        ARTIFACTORY_CREDENTIALS = credentials('artifactory-credentials')
    }

    options {
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: "14", numToKeepStr: ''))
        timestamps()
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew assemble --parallel'
            }
        }

        stage('test') {
            steps {
                sh './gradlew micrometer-osgi-test:test --parallel'
            }
        }

        stage('publish') {
            steps {
                sh './gradlew artifactoryPublish --parallel'
            }
        }
    }
}
