pipeline {
  agent {
    docker {
      image 'maven'
      args '-v /var/run/docker.sock:/var/run/docker.sock -v ${PWD}/.m2:/root/.m2 --network clustercontrol-net'
    }    
  }
  
  environment {
    SETTINGS_XML = credentials('settings.xml')
  }

  stages {
    stage('Create environment') {
      steps {
        sh 'curl -sX POST http://clustercontrol:8080/marketplace/deploy/kafka/0.11.0.1?wait=true -H "Content-Type: application/json" -H "Accept: text/html" -d "{\\"uniqueId\\":\\"$HOSTNAME\\"}"'
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
    stage('Connect to environment') {
      steps {
        sh 'curl --unix-socket /var/run/docker.sock -X POST http:/v1.33/networks/kafka-net-$HOSTNAME/connect -H "Content-Type: application/json" -d "{\\"Container\\":\\"$HOSTNAME\\"}"'
      }
    }
    stage('Test') {
      steps {
        sh 'mvn -DbrokerList=kafka-dc1-$HOSTNAME:9092,kafka-dc2-$HOSTNAME:9092,kafka-dc3-$HOSTNAME:9092 surefire:test'
      }
      post {
        always {
          sh 'curl --unix-socket /var/run/docker.sock -X POST http:/v1.33/networks/kafka-net-$HOSTNAME/disconnect -H "Content-Type: application/json" -d "{\\"Container\\":\\"$HOSTNAME\\",\\"force\\":true}"'
          sh 'sleep 5'
          sh 'curl -sX POST http://clustercontrol:8080/marketplace/undeploy/kafka/0.11.0.1?wait=true -H "Content-Type: application/json" -H "Accept: text/html" -d "{\\"uniqueId\\":\\"$HOSTNAME\\"}"'            
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
