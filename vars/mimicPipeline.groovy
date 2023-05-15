def call(body) {
    // evaluate the body block, and collect configuration into the object
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()
    pipeline {
        agent any
        stages {
               stage('Compile') {
            steps {

                echo 'Clean previous build output'
                sh './gradlew clean'

                echo 'Compile pipelineParams.app'
                sh './gradlew :pipelineParams.app:build'
                
                echo 'Compile pipelineParams.base'
                sh './gradlew :pipelineParams.base:build'
                
                echo 'Compile pipelineParams.common'
                sh './gradlew :pipelineParams.common:build'
                
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

