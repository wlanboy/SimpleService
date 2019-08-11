pipeline {
  agent any
  options {
    buildDiscarder(logRotator(numToKeepStr: '1'))
  }
  environment {
    LOGSTASH = 'nuc:5044'
  }  
  stages {
    stage('Git') {
      steps {
        git(url: 'https://github.com/wlanboy/SimpleService.git', branch: 'master')
      }
    }
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
  }
}