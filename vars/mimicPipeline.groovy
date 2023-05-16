def call(Map pipelineParams) {
    pipeline {
        agent any
        stages {
               stage('Compile') {
            steps {

                echo 'Clean previous build output'
                sh './gradlew clean'

                echo 'Compile pipelineParams.app'
                sh "./gradlew :${pipelineParams.app}:build"
                
                echo 'Compile pipelineParams.base'
                sh "./gradlew :${pipelineParams.base}:build"
                
                echo 'Compile pipelineParams.common'
                sh "./gradlew :${pipelineParams.common}:build"
                
            }
        }

            stage ('test') {
                steps {
               echo 'running tests on pipelineParams.app'
               sh "./gradlew cleanTest ${pipelineParams.app}:test --stacktrace"
                  
               echo 'running tests on pipelineParams.base'
               sh "./gradlew cleanTest ${pipelineParams.base}:test --stacktrace"   
                  
               echo 'running tests on pipelineParams.common'
               sh "./gradlew cleanTest ${pipelineParams.common}:test --stacktrace"    
                }
            }
}
}
}

