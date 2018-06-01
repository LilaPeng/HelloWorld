stage('build'){
    node {
     git 'https://gitee.com/LilaPeng/HelloWorld.git'
     //withEnv(["PATH+MAVEN=${tool 'mvn'}/bin"]) {
          //sh "mvn -B –Dmaven.test.failure.ignore=true clean package"
          //sh "mvn clean package"
     //}
     def mvnHome = tool 'mvn'
     sh "${mvnHome}/bin/mvn -B -Dmaven.test.failure.ignore verify"
     stash excludes: 'target/', includes: '**', name: 'source'
    }
}

stage('test'){
    parallel 'integration': { 
     node {
          unstash 'source'
          withEnv(["PATH+MAVEN=${tool 'mvn'}/bin"]) {
               sh "mvn clean verify"
          }
     }
    }, 'quality': {
     node {
          unstash 'source'
          }
     }
}

stage('approve'){
    timeout(time: 7, unit: 'DAYS') {    
    input 'Ready to go?'
    // rest as before
    }
    node {
     unstash 'source'
         }
}

