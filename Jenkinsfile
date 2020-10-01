pipeline {
  agent any
  stages {
    stage('initialize') {
      steps {
        bat 'echo %~dp0'
      }
    }

    stage('build') {
      steps {
        bat "cd /d \"${TARGET_DIR}\""
        bat 'gradle build -x test'
      }
    }

    stage('unit test') {
      steps {
        echo 'unit test'
      }
    }

    stage('deploy') {
      steps {
        echo 'deploy'
      }
    }

  }
  environment {
    TARGET_DIR = 'C:\\Jenkins\\workspace\\oreManagement-JenkinsTest_master'
  }
}