import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine



pipeline {
    environment {
        jsonContent = ''
        jsonNexusContent = ''
        template = ''
        nexusURL = "${nexusURL}"
        JAVA_HOME = "/usr"
        //relative path of the pom.xml you want to read contents from.
    }

    agent {label 'Node1'}
    stages {
        stage("Set up Files") {
            steps {
                sh """
                echo '<?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                    <groupId>groupId</groupId>
                    <artifactId>HTTPGateway</artifactId>
                    <version>1.0-SNAPSHOT</version>
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.apache.maven.plugins</groupId>
                                <artifactId>maven-surefire-plugin</artifactId>
                                <version>2.18.1</version>
                            </plugin>
                        </plugins>
                    </build>
                    <dependencies>
                        <dependency>
                            <groupId>org.apache.geronimo.specs</groupId>
                            <artifactId>geronimo-servlet_3.0_spec</artifactId>
                            <version>1.0</version>
                        </dependency>
                        <dependency>
                            <groupId>org.apache.maven.plugins</groupId>
                            <artifactId>maven-surefire-plugin</artifactId>
                            <version>2.18.1</version>
                        </dependency>
                    </dependencies>
                </project>' > pom.xml
                """
            }
        }

        stage("Nexus Scan") {
            steps {
                script {
                    pomList = sh(script: "find . -name 'pom.xml'", returnStdout: true).split("\n")
                    echo "POM list : ${pomList}"
                    versionMap = generateMap()
                    for(pom in pomList) {
                        readPom = readMavenPom file: pom;
                        // Read dependency names and versions from a pom.xml by using the Pipeline Utility Steps plugin
                        for ( dependency in readPom.dependencies) {
                            versionMap << [(dependency.artifactId):dependency.version]
                        }
                    }
                    //Scans Nexus server for available versions of declared dependencies.
                    Map<String, Set> ComparedDependencies = findVersionsOnNexus(versionMap, env.nexusURL)
                    
                    String fileContent = "{\"customProperties\":{}, \"jenkinsBuildNumber\":{}}"
                    jsonNexusContent = dependencyJsonWriter(versionMap, ComparedDependencies, env.BUILD_NUMBER, fileContent )
                }
            }
        }
        stage("Templating") {
            steps {
                script {
                    templateText = '''
                    <table>
                        <tr>
                            <%= data.jenkinsBuildNumber %>
                        </tr>
                        <tr>
                            <td>Current version</td>
                            <td>Available versions</td>
                        </tr>
                        <% for(r in data.customProperties) { %>
                        <tr>
                            <td><%= r.key %></td>
                            <td><%= r.value %></td>
                        </tr>
                        <% } %>
                    </table>
                    '''
                    def slurpedJson = new JsonSlurper().parseText(jsonNexusContent)

                    // Create Engine
                    def engine = new groovy.text.SimpleTemplateEngine()
                    //Create template and generate text through make() method
                    def template = engine.createTemplate(templateText).make(data: slurpedJson)
                    println feedTemplate = template.toString()
                }
                writeFile file: './dependencyComparison.html', text: feedTemplate
                archiveArtifacts(artifacts: 'dependencyComparison.html', fingerprint: true)
            }
        }
    }
}

static def findVersionsOnNexus(Map versionMapIn, String nexusURL) {
    def versionMapOut = versionMapIn.clone()
    Map<String, Set<String>> nexusSet = new HashMap<String, Set<String>>()
    List<String> RepoNames = []
    
    versionMapOut.each {
        // def nexusApiUrlRequest = new URL("${nexusURL}/service/rest/beta/search?name=${it.key}").openConnection()
        def nexusApiUrlRequest = new URL("${nexusURL}/service/rest/v1/search?name=${it.key}").openConnection()
        println("nexusApiUrlRequest is: " + nexusApiUrlRequest)
        def nexusApiRC = nexusApiUrlRequest.getResponseCode()
        def responseOutput = nexusApiUrlRequest.getInputStream().getText()
        if (nexusApiRC.equals(200)) {
            println "Search returned values"
        } else {
            println "Error: ${nexusApiUrlRequest.getResponseCode()}"
            return 1
        }
        def json = new JsonSlurper().parseText(responseOutput)
        RepoNames.addAll(json.items)
        nexusApiUrlRequest.disconnect()
    }
        
    RepoNames.each {
        String version = it.version
        String lib = it.name
        if (versionMapOut.containsKey(lib)){
            //Used treeset to guarantee insertion order, order comes from API return
            Set set = new TreeSet<String>()
            set.add(versionMapOut.get(lib))
            set.add(version)
            versionMapOut.replace(lib, set)
        } else {
            def set = new HashSet<String>()
            set.add(version)
            versionMapOut.put(lib, version)
        }
    }
    return versionMapOut
}

static def generateMap() {
    return [:]
}

// Writes current and available dependencies to a json file.
static def dependencyJsonWriter(Map<String, String> buildVersionMap, Map<String, Set> repoNames, buildNumber, paramsJsonfile) {
    String jsonText = paramsJsonfile
    def slurper = new JsonSlurper().parseText(jsonText)
    def json = new JsonBuilder(slurper)
    json.content.jenkinsBuildNumber.putAt("Jenkins Build Number", "${buildNumber}")
    buildVersionMap.each {
        json.content.customProperties.putAt("Dependency: ${it.key} Current version: ${it.value}", "Available versions: ${repoNames[it.key]}")
    }
    json = json.toString()
    return json
}