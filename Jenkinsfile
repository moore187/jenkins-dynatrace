import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

pipeline {
    environment {
        jsonContent = ''
        jsonNexusContent = ''
<<<<<<< HEAD
        nexusURL = 'http://ec2-3-8-93-29.eu-west-2.compute.amazonaws.com:8081'
=======
        nexusURL = 'http://ec2-3-8-15-34.eu-west-2.compute.amazonaws.com:8081'
>>>>>>> 7258bef04d450cf8b26e9e00426aea297962a807
        JAVA_HOME = "/usr/lib/jvm/java"
        //relative path of the pom.xml you want to read contents from.
        pomContents = ''
    }

    agent {label 'Node1'}
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
                            <groupId>org.apache.geronimo.specs</groupId>
                            <artifactId>geronimo-servlet_3.0_spec</artifactId>
                            <version>1.0</version>
                        </dependency>
                    </dependencies>
                </project>' > pom.xml"""
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
                // writeFile file: './pom.xml', text: pomContents
            }
        }

        stage("Nexus Scan") {
            steps {
                script {
<<<<<<< HEAD
                    pomContents = readFile file: './pom.xml'
                    println(pomContents.toString())
                    //Scans pom.xml file to get dependency names and versions and puts them into versionMapNew.
                    def versionMapNew = [:]
                    def xml = new XmlParser().parseText(pomContents)
                    def dependencyListGPath = xml["dependencies"]["dependency"]
                    for (dependency in dependencyListGPath) {
                        dependency.children().each {
                            def childString = it.name().toString()
                            if (childString.indexOf("version") >= 0) {
                                versionMapNew << [(dependency["artifactId"].text()):it.text()]
                            }
                        }
                    }
                    println(versionMapNew.toString())
                    //Scans Nexus server for available versions of declared dependencies.
                    Map<String, Set> ComparedDependencies = findVersionsOnNexus(versionMapNew, env.nexusURL)
                    String fileContents = readFile file:'./file'
                    jsonNexusText = urbancodeFileWriter(versionMapNew, ComparedDependencies, fileContents)
                    jsonNexusContent = jsonNexusText
                }
                writeFile file: './nexusFile', text: jsonNexusContent
=======
                    PomList = populatePomList(env.WORKSPACE)
                    Map<String,String> pomVersions = populateVersionMap(PomList)
                    Map<String, Set> ComparedDependencies = findVersionsOnNexus(pomVersions, env.nexusURL)
                    Map<String,String> pomVersionsNew = populateVersionMap(PomList)
                    jsonNexusText = urbancodeFileWriter(pomVersionsNew, ComparedDependencies, env.WORKSPACE)
                    jsonNexusContent = jsonNexusText
                }
                writeFile file: './nexusFile', text: jsonNexusContent

>>>>>>> 7258bef04d450cf8b26e9e00426aea297962a807
            }
        }

        stage("Create New Deployment Version") {
            steps{
<<<<<<< HEAD
                //Create a new version of the deployDynatrace component on Urbancode.
                withCredentials([usernamePassword(credentialsId: 'yuliaucd', usernameVariable: 'UCDUSER', passwordVariable: 'UCDPASS')]) {
                    sh """udclient -username '$UCDUSER' -password '$UCDPASS' \
                    -weburl http://${urbancodeserver} \
                    createVersion \
                    -component deployDynatrace \
                    -name ${version}"""
                }
=======
                sh """udclient -username '${username}' -password '${password}' \
                -weburl http://${urbancodeserver} \
                createVersion \
                -component deployDynatrace \
                -name ${version}"""
>>>>>>> 7258bef04d450cf8b26e9e00426aea297962a807
            }
        }

        stage("Add Files to Deployment Version") {
            steps{
<<<<<<< HEAD
                //Upload the created nexusFile into the deployDynatrace component version created in the previous step.
                withCredentials([usernamePassword(credentialsId: 'yuliaucd', usernameVariable: 'UCDUSER', passwordVariable: 'UCDPASS')]) {
                    sh """udclient -username '$UCDUSER' -password '$UCDPASS' \
                    -weburl http://${urbancodeserver} \
                    addVersionFiles \
                    -component deployDynatrace \
                    -version ${version} \
                    -base $WORKSPACE \
                    -include nexusFile"""
                }
=======
                sh """udclient -username '${username}' -password '${password}' \
                -weburl http://${urbancodeserver} \
                addVersionFiles \
                -component deployDynatrace \
                -version ${version} \
                -base $WORKSPACE \
                -include nexusFile"""
>>>>>>> 7258bef04d450cf8b26e9e00426aea297962a807
            }
        }
        

        stage("Start UCD Deployment") {
            steps{
<<<<<<< HEAD
                //Creates a file called process.json that will be sent as a put request to Urbancode to in ititiate deployment.
                withCredentials([usernamePassword(credentialsId: 'yuliaucd', usernameVariable: 'UCDUSER', passwordVariable: 'UCDPASS')]) {
                    script{
                        String jsonText = "${env.UCDJSON}"
                        def slurper = new JsonSlurper().parseText(jsonText)
                        def json = new JsonBuilder(slurper)

                        json.content.putAt("application", "${env.applicationName}")
                        json.content.putAt("applicationProcess", "${env.applicationProcess}")
                        json.content.putAt("environment", "${env.environment}")
                        json.content.versions[0].putAt("component", "deployDynatrace")
                        json.content.versions[0].putAt("version", "latest")

                        println(json.toPrettyString())                    
                        processJson = json.toString()

                        json = null
                    }
                writeFile file: './process.json', text: processJson

               sh """udclient -username $UCDUSER -password $UCDPASS \
               -weburl http://${urbancodeserver} \
               requestApplicationProcess process.json"""
                }
=======
                script{
                    String jsonText = "${env.UCDJSON}"
                    def slurper = new JsonSlurper().parseText(jsonText)
                    def json = new JsonBuilder(slurper)

                    json.content.putAt("application", "${env.applicationName}")
                    json.content.putAt("applicationProcess", "${env.applicationProcess}")
                    json.content.putAt("environment", "${env.environment}")
                    json.content.versions[0].putAt("component", "deployDynatrace")
                    json.content.versions[0].putAt("version", "latest")

                    println(json.toPrettyString())                    
                    processJson = json.toString()

                    json = null
                }
                writeFile file: './process.json', text: processJson

               sh """udclient -username ${username} -password ${password} \
               -weburl http://${urbancodeserver} \
               requestApplicationProcess process.json"""
>>>>>>> 7258bef04d450cf8b26e9e00426aea297962a807
            }
        }
    }
}

<<<<<<< HEAD
static def populatePomList(String workspace) {
=======
static def populatePomList(workspace) {
>>>>>>> 7258bef04d450cf8b26e9e00426aea297962a807
    def pomList = []
    def dir = new File(workspace)
    dir.eachFileRecurse (FileType.FILES) { file ->
        if (file.getName() == "pom.xml") {
            pomList << file
        }
    }
    return pomList
}

<<<<<<< HEAD
// static def populateVersionMap(String workspace, ArrayList<File> pomList, String pomContents) {
//     def versionMapNew = [:]
//     def pomList = ['pom.xml']
//     File file = new File('./pom.xml')
//     file << pomContents
//     pomList.each {
//         def currentPom = new File(it.toString())
//         def xml = new XmlParser().parse(currentPom)
//         def dependencyListGPath = xml["dependencies"]["dependency"]
//         for (dependency in dependencyListGPath) {
//             dependency.children().each {
//                 def childString = it.name().toString()
//                 if (childString.indexOf("version") >= 0) {
//                     versionMapNew << [(dependency["artifactId"].text()):it.text()]
//                 }
//             }
//         }
//     }
//     return versionMapNew
// }
=======
static def populateVersionMap(ArrayList<File> pomList) {
    def versionMapNew = [:]
    pomList.each {
        def currentPom = new File(it.toString())
        def xml = new XmlParser().parse(currentPom)
        def dependencyListGPath = xml["dependencies"]["dependency"]
        for (dependency in dependencyListGPath) {
            dependency.children().each {
                def childString = it.name().toString()
                if (childString.indexOf("version") >= 0) {
                    versionMapNew << [(dependency["artifactId"].text()):it.text()]
                }
            }
        }
    }
    return versionMapNew
}
>>>>>>> 7258bef04d450cf8b26e9e00426aea297962a807

static def findVersionsOnNexus (Map versionMap, String nexusURL) {
    Map<String, Set<String>> nexusSet = new HashMap<String, Set<String>>()
    List<String> RepoNames = []
    
    versionMap.each {
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
    }
        
    RepoNames.each {
        String version = it.version
        String lib = it.name
        if (versionMap.containsKey(lib)){
            //Used treeset to guarantee insertion order, order comes from API return
            Set set = new TreeSet<String>()
            set.add(versionMap.get(lib))
            set.add(version)
            versionMap.replace(lib, set)
        } else {
            def set = new HashSet<String>()
            set.add(version)
            versionMap.put(lib, version)
        }
    }
    return versionMap
}

static def urbancodeFileWriter(Map<String, String> buildVersionMap, Map<String, Set> repoNames, fileContents) {
    def jsonText = fileContents
    def slurper = new JsonSlurper().parseText(jsonText)
    def json = new JsonBuilder(slurper)
    buildVersionMap.each {
        json.content.customProperties.putAt("Current ${it.key} Version: ${it.value}", "Available ${it.key} Versions: ${repoNames[it.key]}")
    }
    json = json.toString()
    return json
}
