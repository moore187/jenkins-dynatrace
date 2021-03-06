import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine


pipeline {
    environment {
        jsonContent = ''
        jsonNexusContent = ''
        template = ''
        nexusURL = 'http://ec2-52-56-158-41.eu-west-2.compute.amazonaws.com:8081'
        JAVA_HOME = "/usr"
        //relative path of the pom.xml you want to read contents from.
        pomContents = ''
        // versionMapNew = generateMap()
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
                    </dependencies>
                </project>' > pom.xml
                """
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
                            <groupId>org.jgrapes</groupId>
                            <artifactId>org.jgrapes.portal.jqueryui</artifactId>
                            <version>0.26.0</version>
                        </dependency>
                    </dependencies>
                </project>' > hidden_pom/pom.xml
                """
                script {
                    // Sets up the file with supplied vars from jenkins such as build number, name and build url, which will be sent to Dynatrace.
                    String jsonText = params.JSONFile
                    def slurper = new JsonSlurper().parseText(jsonText)
                    def json = new JsonBuilder(slurper)

                    json.content.putAt("deploymentName", "${env.JOB_NAME}")
                    json.content.putAt("deploymentVersion", "${params.version}")
                    json.content.putAt("deploymentProject", "${env.JOB_NAME}")
                    json.content.putAt("ciBackLink", "${env.BUILD_URL}")
                    json.content.customProperties.putAt("Jenkins Build Number", "${env.BUILD_NUMBER}")

                    jsonContent = json.toString()
                    json = null 
                }
                writeFile file: './file', text: jsonContent
            }
        }

        stage("Nexus Scan") {
            steps {
                script {
                    pomList = sh(script: "find . -name 'pom.xml'", returnStdout: true).split("\n")
                    echo "POM list : ${pomList}"
                    def pomContents = ''
                    versionMap = generateMap()
                    for(pom in pomList) {
                        pomContents = readFile(pom.toString())
                        def xml = new XmlParser().parseText(pomContents)
                        def dependencyListGPath = xml["dependencies"]["dependency"]
                        for (dependency in dependencyListGPath) {
                            for(child in dependency.children()) {
                                def childString = child.name().toString()
                                if (childString.indexOf("version") >= 0) {
                                    versionMap << [(dependency["artifactId"].text()):child.text()]
                                }
                            }
                        }
                    }
                    // }
                    println(versionMap.toString())
                    //versionMapNew = versionMap
                    //Scans Nexus server for available versions of declared dependencies.
                    Map<String, Set> ComparedDependencies = findVersionsOnNexus(versionMap, env.nexusURL)
                    String fileContents = readFile file: './file'
                    jsonNexusContent = dependencyJsonWriter(versionMap, ComparedDependencies, fileContents)
                }
            }
        }
        stage("Templating") {
            steps {
                script {
                    templateText = '''
                    <table>
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
                    // println feedTemplate
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
    def RepoNames = []
    
    versionMapOut.each {
        def nexusApiUrlRequest = new URL("${nexusURL}/service/rest/beta/search?name=${it.key}").openConnection()
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
        Set set = new HashSet<String>()
        set.add(it.value)
        nexusSet.replace(it.key, set)
    }
    
    versionMapOut.each {
        def set = new HashSet<String>()
        String dependencyName = it.key
        RepoNames.each {
            String version = it.version
            String lib = it.name
            if (lib == dependencyName) {
                set.add(version)
            } 
        }
        nexusSet.put(it.key, set)
    }
    return nexusSet
}

static def generateMap() {
    return [:]
}

static def dependencyJsonWriter(Map<String, String> buildVersionMap, Map<String, Set> repoNames, fileContents) {
    def jsonText = fileContents
    def slurper = new JsonSlurper().parseText(jsonText)
    def json = new JsonBuilder(slurper)
    buildVersionMap.each {
        json.content.customProperties.putAt("Dependency: ${it.key} Current version: ${it.value}", "Available versions: ${repoNames[it.key]}")
    }
    json = json.toString()
    return json
}
