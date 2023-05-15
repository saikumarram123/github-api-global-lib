def call(Map pipelineParams) {

    pipeline {
        agent any
         tools {
        maven 'M3'
        jdk 'Java 17'
        }
        stages {
               stage('Compile') {
            steps {

                echo 'Clean previous build output'
                sh './gradlew clean'

                echo 'Compile pipelineParams.app'
                sh './gradlew :app:compileJava :pipelineParams.app:compileTestJava --stacktrace'
                
                echo 'Compile pipelineParams.base'
                sh './gradlew :base:compileJava :pipelineParams.base:compileTestJava --stacktrace'
                
                echo 'Compile pipelineParams.common'
                sh './gradlew :common :pipelineParams.common:compileTestJava --stacktrace'
                
            }
        }

            stage ('test') {
                steps {
               echo 'running tests on pipelineParams.app'
               sh './gradlew cleanTest pipelineParams.app:test --stacktrace'
                  
               echo 'running tests on pipelineParams.base'
               sh './gradlew cleanTest pipelineParams.base:test --stacktrace'   
                  
               echo 'running tests on pipelineParams.common'
               sh './gradlew cleanTest pipelineParams.common:test --stacktrace'    
                }
            }
      stage('Package') {
        steps {
                echo 'Building jars for app'
                sh './gradlew :pipelineParams.app:bootJar --stacktrace'
          
                echo 'Building jars for pipelineParams.base'
                sh './gradlew :pipelineParams.base:bootJar --stacktrace'
          
                echo 'Building jars for pipelineParams.common'
                sh './gradlew :pipelineParams.common:bootJar --stacktrace'
        post {
            failure {
                mail to: pipelineParams.email, subject: 'Pipeline failed', body: "${env.BUILD_URL}"
            }
        }
        }
    }
}
}
}

