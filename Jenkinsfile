@Library('nexus-dependency-search')_
import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine


pipeline {
    agent any
    environment {
        nexusURL = 'http://ec2-52-56-158-41.eu-west-2.compute.amazonaws.com:8081'
        JAVA_HOME = "/usr"
    }
    stages {
        stage ('Dependency search') {
            steps {
                sh "echo Looking for dependencies in Nexus"
                nexusDepsTool nexusURL, JAVA_HOME
            }
        }
    }
}
