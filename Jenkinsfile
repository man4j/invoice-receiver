pipeline {
  agent {
    docker {
      image 'maven'
      args '-v /var/run/docker.sock:/var/run/docker.sock -v ${PWD}/.m2:/root/.m2 --network clustercontrol-net --network kafka-net-ci1 -e brokerList=kafka-dc1-ci1:11092,kafka-dc2-ci1:11092,kafka-dc3-ci1:11092'
    }    
  }
  
  environment {
    SETTINGS_XML = credentials('settings.xml')
  }

  stages {
    stage('Create environment') {
      steps {
        sh 'curl -sX POST http://clustercontrol:8080/marketplace/deploy/kafka/0.11.0.1?wait=true -H "Content-Type: application/json" -H "Accept: text/html" -d \'{"uniqueId":"ci1","brokerPort":"11092"}\''
      }
    }
    stage('Clean') {
      steps {
        sh 'mvn clean:clean'
      }
    }
    stage('Resources') {
      steps {
        sh 'mvn resources:resources resources:testResources'
      }
    }
    stage('Compile') {
      steps {
        sh 'mvn compiler:compile compiler:testCompile'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn surefire:test'
      }
      post {
        always {
          sh 'curl -sX POST http://clustercontrol:8080/marketplace/undeploy/kafka/0.11.0.1?wait=true -H "Content-Type: application/json" -H "Accept: text/html" -d \'{"uniqueId":"ci1"}\''            
        }
      }
    }
    stage('Package') {
      steps {
        sh 'mvn jar:jar shade:shade'
      }
    }
    stage('Image') {
      steps {
        sh 'mvn -s $SETTINGS_XML docker:build -DpushImage'
      }
    }
  } 
  
  post {
    always {
      junit 'target/surefire-reports/*.xml'    
    }
  }
}
