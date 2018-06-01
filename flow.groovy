stage 'build'
node {
     git 'https://gitee.com/LilaPeng/HelloWorld.git'
     withEnv(["PATH+MAVEN=${tool 'mvn'}/bin"]) {
          sh "mvn -B –Dmaven.test.failure.ignore=true clean package"
     }
     stash excludes: 'target/', includes: '**', name: 'source'
}
stage 'test'
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
          withEnv(["PATH+MAVEN=${tool 'mvn'}/bin"]) {
               sh "mvn sonar:sonar"
          }
     }
}
stage 'approve'
timeout(time: 7, unit: 'DAYS') {
     input message: 'Do you want to deploy?', submitter: 'ops'
}
stage name:'deploy', concurrency: 1
node {
     unstash 'source'
     withEnv(["PATH+MAVEN=${tool 'mvn'}/bin"]) {
          sh "mvn cargo:deploy"
     }
}
