node('master'){
  load 'simple.groovy'
  git url: 'https://gitee.com/LilaPeng/HelloWorld.git' 
  def mvnHome = tool 'mvn'
  sh "${mvnHome}/bin/mvn -B verify"
  
}