def devQAStaging() {
    env.PATH="${tool 'mvn'}/bin:${env.PATH}"
  
    stage ('Dev'){
    sh 'mvn -o clean package'
    archiveArtifacts 'target/helloworld-1.0-SNAPSHOT.jar'
    }
    stage ('QA'){
    parallel(longerTests: {
        runWithServer {url ->
            sh "mvn -o -f sometests/pom.xml test -Durl=${url} -Dduration=30"
        }
    }, quickerTests: {
        runWithServer {url ->
            sh "mvn -o -f sometests/pom.xml test -Durl=${url} -Dduration=20"
        }
    })
    }
    stage ('Staging'){
    deploy 'target/helloworld-1.0-SNAPSHOT.jar', 'staging'
    } 
}

def production() {
    input message: "Does http://localhost:8080/staging/ look good?"
    try {
        checkpoint('Before production')
    } catch (NoSuchMethodError _) {
        echo 'Checkpoint feature available in Jenkins Enterprise by CloudBees.'
    }
    stage('Production'){
    node('master') {
        sh 'curl -I http://localhost:8080/staging/'
        unarchiveArtifacts mapping: ['target/helloworld-1.0-SNAPSHOT.jar' : 'helloworld-1.0-SNAPSHOT.war']
        deploy 'helloworld-1.0-SNAPSHOT.jar', 'production'
        echo 'Deployed to http://localhost:8080/production/'
    }
  }
}

def deploy(war, id) {
    sh "cp ${jar} /tmp/webapps/${id}.war"
}

def undeploy(id) {
    sh "rm /tmp/webapps/${id}.war"
}

def runWithServer(body) {
    def id = UUID.randomUUID().toString()
    deploy 'target/helloworld-1.0-SNAPSHOT.jar', id
    try {
        body.call "http://localhost:8080/${id}/"
    } finally {
        undeploy id
    }
}

return this;
