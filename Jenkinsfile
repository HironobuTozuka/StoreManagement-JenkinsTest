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
        bat "\"${GRADLE}\" build -x test"
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
    GRADLE = 'C:\\Program Files\\Java\\gradle-6.6.1\\bin\\gradle'
  }
}