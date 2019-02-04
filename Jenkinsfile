import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

pipeline {
    environment {
        jsonContent = ''
        nexusURL = 'http://ec2-3-8-162-84.eu-west-2.compute.amazonaws.com:8081'
        JAVA_HOME = "/usr/bin/jvm/java"
    }

    agent any
    stages {
        stage("Set up File") {
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
                            <groupId>Testing</groupId>
                            <artifactId>Testing</artifactId>
                            <version>1.0</version>
                        </dependency>
                    </dependencies>
                </project>' > pom.xml"""
            
                script {
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
                    PomList = populatePomList()
                    Map<String,String> pomVersions = populateVersionMap(PomList)
                    Map<String, Set> ComparedDependencies = findVersionsOnNexus(pomVersions, env.nexusURL)
                    Map<String,String> pomVersionsNew = populateVersionMap(PomList)
                    jsonText = urbancodeFileWriter(pomVersionsNew, ComparedDependencies, env.WORKSPACE)

                    jsonContent = jsonText
                }
                writeFile file: './file', text: jsonContent
            }
        }

        stage("Create New Deployment Version") {
            steps{
                sh """udclient -username ${username} -password ${password}
                -weburl https://${urbancodeserver}/
                createVersion
                -component deployDynatrace
                -name ${version}"""
            }
        }

        stage("Add Files to Deployment Version") {
            steps{
                sh """udclient -username ${username} -password ${password}
                -weburl https://${urbancodeserver}/
                addVersionFiles
                -component deployDynatrace
                -version ${version}
                -base $WORKSPACE"""
            }
        }

        stage("Start UCD Deployment") {
            steps{
                script{
                    String jsonText = "${env.UCDJSON}"
                    def slurper = new JsonSlurper().parseText(jsonText)
                    def json = new JsonBuilder(slurper)

                    json.content.putAt("application", "${env.applicationName}")
                    json.content.putAt("applicationProcess", "Dynatrace Deployment")
                    json.content.putAt("environment", "${env.environment}")
                    json.content.versions[0].putAt("component", "deployDynatrace")
                    json.content.versions[0].putAt("version", "latest")

                    println(json.toPrettyString())

                    File file = new File("./process.json")
                    file << json.toString()

                    json = null
                }

               // sh """udclient -username ${username} -password ${password}
               // -weburl https://${urbancodeserver}/
               // requestApplicationProcess process.json"""
            }
        }
    }
}

static def populatePomList() {
    def pomList = []
    def dir = new File(".")
    dir.eachFileRecurse (FileType.FILES) { file ->
        if (file.getName() == "pom.xml") {
            pomList << file
        }
    }
    println("PomList: ${pomList.toString()}")
    return pomList
}

static def populateVersionMap(ArrayList<File> pomList) {
    def versionMapNew = [:]
    pomList.each {
        def currentPom = new File(it.toString())
        def xml = new XmlParser().parse(currentPom)
        def dependencyListGPath = xml.dependencies.dependency
        for (dependency in dependencyListGPath) {
            dependency.children().each {
                def childString = it.name().toString()
                if (childString.indexOf("version") >= 0) {
                    versionMapNew << [(dependency.artifactId.text()):it.text()]
                }
            }
        }
    }
    println("VersionMap: ${versionMapNew.toString()}")
    return versionMapNew
}

static def findVersionsOnNexus (Map versionMap, String nexusURL) {
    Map<String, Set<String>> nexusSet = new HashMap<String, Set<String>>()
    List<String> RepoNames = []
    
    versionMap.each {
        def nexusApiUrlRequest = new URL("${nexusURL}/service/rest/v1/search?name=${it.key}").openConnection()
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

        println("${lib}:${version}")

        if (versionMap.containsKey(lib)){
            //Used treeset to guarantee insertion order, order comes from API return
            Set set = new TreeSet<String>()
            set.addAll(versionMap.get(lib))
            set.add(version)
            versionMap.replace(lib, set)
        } else {
            def set = new HashSet<String>()
            set.add(version)
            versionMap.put(lib, version)
        }
        println("VersionMap: ${versionMap.toString()}")
    }
    return versionMap
}

static def urbancodeFileWriter(Map<String, String> buildVersionMap, Map<String, Set> repoNames, String workspace) {
    def jsonText = new File(workspace + '/file').getText()
    def slurper = new JsonSlurper().parseText(jsonText)
    def json = new JsonBuilder(slurper)

    buildVersionMap.each {println(it)}

    buildVersionMap.each {
        json.content.customProperties.putAt("Current ${it.key} Version: ${it.value}", "Available ${it.key} Versions: ${repoNames[it.key]}")
    }
    json = json.toString()
    return json
}
