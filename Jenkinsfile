@Library('hello_you_library')_
import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine


pipeline {
    agent any
    environment {
        server = 'Super server'
        port = '8080808080'
    }
    stages {
        stage ('Greeting') {
            echo 'Hello world'
            helloWorld 'Five!Any time travelling tonight?'
        }
    }
}

// nexusDepsTool { 
//     nexusURL = 'http://ec2-52-56-158-41.eu-west-2.compute.amazonaws.com:8081'
//     JAVA_HOME = "/usr"
// }