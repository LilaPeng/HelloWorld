node('master'){
  git url: 'https://github.com/jglick/simple-maven-project-with-tests.git' 
  def mvnHome = tool 'mvn'
  sh "${mvnHome}/bin/mvn -B verify"
}
