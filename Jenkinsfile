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
        pomContents = ''
    }

    agent {label 'Node1'}
    stages {
        stage("Set up Files") {
            steps {
                sh """
                echo '<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>

    <name>JUnit</name>
    <description>JUnit is a unit testing framework for Java, created by Erich Gamma and Kent Beck.</description>
    <url>http://junit.org</url>
    <inceptionYear>2002</inceptionYear>
    <organization>
        <name>JUnit</name>
        <url>http://www.junit.org</url>
    </organization>
    <licenses>
        <license>
            <name>Eclipse Public License 1.0</name>
            <url>http://www.eclipse.org/legal/epl-v10.html</url>
            <distribution>repo</distribution>
        </license>
    </licenses>

    <developers>
        <developer>
            <id>dsaff</id>
            <name>David Saff</name>
            <email>david@saff.net</email>
        </developer>
        <developer>
            <id>kcooney</id>
            <name>Kevin Cooney</name>
            <email>kcooney@google.com</email>
        </developer>
        <developer>
            <id>stefanbirkner</id>
            <name>Stefan Birkner</name>
            <email>mail@stefan-birkner.de</email>
        </developer>
        <developer>
            <id>marcphilipp</id>
            <name>Marc Philipp</name>
            <email>mail@marcphilipp.de</email>
        </developer>
    </developers>
    <contributors>
        <contributor>
            <name>JUnit contributors</name>
            <organization>JUnit</organization>
            <email>junit@yahoogroups.com</email>
            <url>https://github.com/junit-team/junit/graphs/contributors</url>
            <roles>
                <role>developers</role>
            </roles>
        </contributor>
    </contributors>

    <mailingLists>
        <mailingList>
            <name>JUnit Mailing List</name>
            <post>junit@yahoogroups.com</post>
            <archive>https://groups.yahoo.com/neo/groups/junit/info</archive>
        </mailingList>
    </mailingLists>

    <prerequisites>
        <maven>3.0.4</maven>
    </prerequisites>

    <scm>
        <connection>scm:git:git://github.com/junit-team/junit.git</connection>
        <developerConnection>scm:git:git@github.com:junit-team/junit.git</developerConnection>
        <url>http://github.com/junit-team/junit/tree/master</url>
      <tag>r4.12</tag>
  </scm>
    <issueManagement>
        <system>github</system>
        <url>https://github.com/junit-team/junit/issues</url>
    </issueManagement>
    <ciManagement>
        <system>jenkins</system>
        <url>https://junit.ci.cloudbees.com/</url>
    </ciManagement>
    <distributionManagement>
        <downloadUrl>https://github.com/junit-team/junit/wiki/Download-and-Install</downloadUrl>
        <snapshotRepository>
            <id>junit-snapshot-repo</id>
            <name>Nexus Snapshot Repository</name>
            <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        </snapshotRepository>
        <repository>
            <id>junit-releases-repo</id>
            <name>Nexus Release Repository</name>
            <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
        </repository>
        <site>
            <id>junit.github.io</id>
            <url>gitsite:git@github.com/junit-team/junit.git</url>
        </site>
    </distributionManagement>

    <properties>
        <jdkVersion>1.5</jdkVersion>
        <project.build.sourceEncoding>ISO-8859-1</project.build.sourceEncoding>
        <arguments />
        <gpg.keyname>67893CC4</gpg.keyname>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.hamcrest</groupId>
            <artifactId>hamcrest-core</artifactId>
            <version>1.3</version>
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
                    //Scans Nexus server for available versions of declared dependencies.
                    Map<String, Set> ComparedDependencies = findVersionsOnNexus(versionMap, env.nexusURL)
                    // Writes current and available dependencies to a json file.
                    jsonNexusContent = dependencyJsonWriter(versionMap, ComparedDependencies, env.BUILD_NUMBER, params.JSONFile )
                }
            }
        }
        stage("Templating") {
            steps {
                script {
                    templateText = '''
                    <table>
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

static def dependencyJsonWriter(Map<String, String> buildVersionMap, Map<String, Set> repoNames, buildNumber, paramsJsonfile) {
    String jsonText = paramsJsonfile
    def slurper = new JsonSlurper().parseText(jsonText)
    def json = new JsonBuilder(slurper)
    buildVersionMap.each {
        json.content.customProperties.putAt("Dependency: ${it.key} Current version: ${it.value}", "Available versions: ${repoNames[it.key]}")
    }
    json.content.customProperties.putAt("Jenkins Build Number", "${buildNumber}")
    json = json.toString()
    return json
}