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
        echo 'unit test'
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
    environment {
      TARGET_DIR = 'C:\\Jenkins\\workspace\\StoreManagement-JenkinsTest'
    }
  }
}
