import groovy.io.FileType
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine



pipeline {
    environment {
        jsonContent = ''
        jsonNexusContent = ''
        template = ''
        nexusURL = 'nexusURL'
        // nexusURL = 'http://ec2-34-242-203-71.eu-west-1.compute.amazonaws.com:8081/nexus'
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

                    <build>
                        <resources>
                            <resource>
                                <directory>${project.basedir}/src/main/resources</directory>
                            </resource>
                            <resource>
                                <directory>${project.basedir}</directory>
                                <includes>
                                    <include>LICENSE-junit.txt</include>
                                </includes>
                            </resource>
                        </resources>
                        <plugins>
                            <!--
                            Both "org.apache" and "org.codehaus" are default providers of MOJO plugins
                            which are especially dedicated to Maven projects.
                            The MOJO stands for "Maven plain Old Java Object".
                            Each mojo is an executable goal in Maven, and a plugin is a distribution of
                            one or more related mojos.
                            For more information see http://maven.apache.org/plugin-developers/index.html

                            The following plugins are ordered according the Maven build lifecycle.
                            http://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html
                            -->
                            <plugin>
                                <!--
                                Checks that the version of user's maven installation is 3.0.4,
                                the JDK is 1.5+, no non-standard repositories are specified in
                                the project, requires only release versions of dependencies of other artifacts.
                                -->
                                <artifactId>maven-enforcer-plugin</artifactId>
                                <version>1.3.1</version>
                                <executions>
                                    <execution>
                                        <id>enforce-versions</id>
                                        <phase>initialize</phase>
                                        <goals>
                                            <goal>enforce</goal>
                                        </goals>
                                        <configuration>
                                            <fail>true</fail>
                                            <rules>
                                                <requireMavenVersion>
                                                    <!-- Some plugin features require a recent Maven runtime to work properly -->
                                                    <message>Current version of Maven ${maven.version} required to build the project
                                                        should be ${project.prerequisites.maven}, or higher!
                                                    </message>
                                                    <version>[${project.prerequisites.maven},)</version>
                                                </requireMavenVersion>
                                                <requireJavaVersion>
                                                    <message>Current JDK version ${java.version} should be ${jdkVersion}, or higher!
                                                    </message>
                                                    <version>${jdkVersion}</version>
                                                </requireJavaVersion>
                                                <requireNoRepositories>
                                                    <message>Best Practice is to never define repositories in pom.xml (use a repository
                                                        manager instead).
                                                    </message>
                                                </requireNoRepositories>
                                                <requireReleaseDeps>
                                                    <message>No Snapshots Dependencies Allowed!</message>
                                                </requireReleaseDeps>
                                            </rules>
                                        </configuration>
                                    </execution>
                                </executions>
                            </plugin>
                            <plugin>
                                <!--
                                Updates Version#id().
                                -->
                                <groupId>com.google.code.maven-replacer-plugin</groupId>
                                <artifactId>replacer</artifactId>
                                <version>1.5.3</version>
                                <executions>
                                    <execution>
                                        <phase>process-sources</phase>
                                        <goals>
                                            <goal>replace</goal>
                                        </goals>
                                    </execution>
                                </executions>
                                <configuration>
                                    <ignoreMissingFile>false</ignoreMissingFile>
                                    <file>src/main/java/junit/runner/Version.java.template</file>
                                    <outputFile>src/main/java/junit/runner/Version.java</outputFile>
                                    <regex>false</regex>
                                    <token>@version@</token>
                                    <value>${project.version}</value>
                                </configuration>
                            </plugin>
                            <plugin><!-- Using jdk 1.5.0_22, package-info.java files are compiled correctly. -->
                                <!--
                                java compiler plugin forked in extra process
                                -->
                                <artifactId>maven-compiler-plugin</artifactId>
                                <version>3.1</version>
                                <configuration>
                                    <encoding>${project.build.sourceEncoding}</encoding>
                                    <source>${jdkVersion}</source>
                                    <target>${jdkVersion}</target>
                                    <testSource>${jdkVersion}</testSource>
                                    <testTarget>${jdkVersion}</testTarget>
                                    <compilerVersion>1.5</compilerVersion>
                                    <showDeprecation>true</showDeprecation>
                                    <showWarnings>true</showWarnings>
                                    <debug>true</debug>
                                    <fork>true</fork>
                                    <compilerArgs>
                                        <arg>-Xlint:unchecked</arg>
                                    </compilerArgs>
                                    <maxmem>128m</maxmem>
                                </configuration>
                            </plugin>
                            <plugin>
                                <groupId>org.codehaus.mojo</groupId>
                                <artifactId>animal-sniffer-maven-plugin</artifactId>
                                <version>1.11</version>
                                <executions>
                                    <execution>
                                        <id>signature-check</id>
                                        <phase>test</phase>
                                        <goals>
                                            <goal>check</goal>
                                        </goals>
                                        <configuration>
                                            <signature>
                                                <groupId>org.codehaus.mojo.signature</groupId>
                                                <artifactId>java15</artifactId>
                                                <version>1.0</version>
                                            </signature>
                                        </configuration>
                                    </execution>
                                </executions>
                            </plugin>
                            <plugin>
                                <!--
                                A plugin which uses the JUnit framework in order to start
                                our junit suite "AllTests" after the sources are compiled.
                                -->
                                <artifactId>maven-surefire-plugin</artifactId>
                                <version>2.17</version>
                                <configuration>
                                    <test>org/junit/tests/AllTests.java</test>
                                    <useSystemClassLoader>true</useSystemClassLoader>
                                    <enableAssertions>false</enableAssertions>
                                </configuration>
                            </plugin>
                            <plugin>
                                <!--
                                This plugin can package the main artifact's sources (src/main/java)
                                in to jar archive. See target/junit-*-sources.jar.
                                -->
                                <artifactId>maven-source-plugin</artifactId>
                                <version>2.2.1</version>
                            </plugin>
                            <plugin>
                                <!--
                                This plugin can generate Javadoc by a forked
                                process and then package the Javadoc
                                in jar archive target/junit-*-javadoc.jar.
                                -->
                                <artifactId>maven-javadoc-plugin</artifactId>
                                <version>2.9.1</version>
                                <configuration>
                                    <stylesheetfile>${basedir}/src/main/javadoc/stylesheet.css</stylesheetfile>
                                    <show>protected</show>
                                    <author>false</author>
                                    <version>false</version>
                                    <detectLinks>false</detectLinks>
                                    <linksource>true</linksource>
                                    <keywords>true</keywords>
                                    <use>false</use>
                                    <windowtitle>JUnit API</windowtitle>
                                    <encoding>UTF-8</encoding>
                                    <locale>en</locale>
                                    <javadocVersion>${jdkVersion}</javadocVersion>
                                    <javaApiLinks>
                                        <property>
                                            <name>api_${jdkVersion}</name>
                                            <value>http://docs.oracle.com/javase/${jdkVersion}.0/docs/api/</value>
                                        </property>
                                    </javaApiLinks>
                                    <excludePackageNames>junit.*,*.internal.*</excludePackageNames>
                                    <verbose>true</verbose>
                                    <minmemory>32m</minmemory>
                                    <maxmemory>128m</maxmemory>
                                    <failOnError>true</failOnError>
                                    <includeDependencySources>true</includeDependencySources>
                                    <dependencySourceIncludes>
                                        <dependencySourceInclude>org.hamcrest:hamcrest-core:*</dependencySourceInclude>
                                    </dependencySourceIncludes>
                                </configuration>
                            </plugin>
                            <plugin>
                                <artifactId>maven-release-plugin</artifactId>
                                <version>2.5</version>
                                <configuration>
                                    <mavenExecutorId>forked-path</mavenExecutorId>
                                    <useReleaseProfile>false</useReleaseProfile>
                                    <arguments>-Pgenerate-docs,junit-release ${arguments}</arguments>
                                    <tagNameFormat>r@{project.version}</tagNameFormat>
                                </configuration>
                            </plugin>
                            <plugin>
                                <artifactId>maven-site-plugin</artifactId>
                                <version>3.3</version>
                                <dependencies>
                                    <dependency>
                                        <groupId>com.github.stephenc.wagon</groupId>
                                        <artifactId>wagon-gitsite</artifactId>
                                        <version>0.4.1</version>
                                    </dependency>
                                    <dependency>
                                        <groupId>org.apache.maven.doxia</groupId>
                                        <artifactId>doxia-module-markdown</artifactId>
                                        <version>1.5</version>
                                    </dependency>
                                </dependencies>
                            </plugin>
                            <plugin>
                                <artifactId>maven-jar-plugin</artifactId>
                                <version>2.4</version>
                                <configuration>
                                    <archive>
                                        <addMavenDescriptor>false</addMavenDescriptor>
                                        <manifest>
                                            <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
                                        </manifest>
                                    </archive>
                                </configuration>
                            </plugin>
                        </plugins>
                    </build>

                    <reporting>
                        <plugins>
                            <plugin>
                                <artifactId>maven-project-info-reports-plugin</artifactId>
                                <version>2.7</version>
                                <configuration>
                                    <dependencyLocationsEnabled>false</dependencyLocationsEnabled>
                                    <!-- waiting for MPIR-267 -->
                                </configuration>
                                <reportSets>
                                    <reportSet>
                                        <reports>
                                            <report>index</report>
                                            <report>dependency-info</report>
                                            <report>modules</report>
                                            <report>license</report>
                                            <report>project-team</report>
                                            <report>scm</report>
                                            <report>issue-tracking</report>
                                            <report>mailing-list</report>
                                            <report>dependency-management</report>
                                            <report>dependencies</report>
                                            <report>dependency-convergence</report>
                                            <report>cim</report>
                                            <report>distribution-management</report>
                                        </reports>
                                    </reportSet>
                                </reportSets>
                            </plugin>
                            <plugin>
                                <artifactId>maven-javadoc-plugin</artifactId>
                                <version>2.9.1</version>
                                <configuration>
                                    <destDir>javadoc/latest</destDir>
                                    <stylesheetfile>${basedir}/src/main/javadoc/stylesheet.css</stylesheetfile>
                                    <show>protected</show>
                                    <author>false</author>
                                    <version>false</version>
                                    <detectLinks>false</detectLinks>
                                    <linksource>true</linksource>
                                    <keywords>true</keywords>
                                    <use>false</use>
                                    <windowtitle>JUnit API</windowtitle>
                                    <encoding>UTF-8</encoding>
                                    <locale>en</locale>
                                    <javadocVersion>${jdkVersion}</javadocVersion>
                                    <javaApiLinks>
                                        <property>
                                            <name>api_${jdkVersion}</name>
                                            <value>http://docs.oracle.com/javase/${jdkVersion}.0/docs/api/</value>
                                        </property>
                                    </javaApiLinks>
                                    <excludePackageNames>junit.*,*.internal.*</excludePackageNames>
                                    <verbose>true</verbose>
                                    <minmemory>32m</minmemory>
                                    <maxmemory>128m</maxmemory>
                                    <failOnError>true</failOnError>
                                    <includeDependencySources>true</includeDependencySources>
                                    <dependencySourceIncludes>
                                        <dependencySourceInclude>org.hamcrest:hamcrest-core:*</dependencySourceInclude>
                                    </dependencySourceIncludes>
                                </configuration>
                                <reportSets>
                                    <reportSet>
                                        <reports>
                                            <report>javadoc</report>
                                        </reports>
                                    </reportSet>
                                </reportSets>
                            </plugin>
                        </plugins>
                    </reporting>

                    <profiles>
                        <profile>
                            <id>junit-release</id>
                            <!--
                            Signs all artifacts before deploying to Maven Central.
                            -->
                            <build>
                                <plugins>
                                    <plugin>
                                        <!--
                                        The goal is to sign all artifacts so that the user may verify them before downloading.
                                        The automatic build system may reuire your key ID, and passphrase specified using system properties:
                                        -Dgpg.passphrase="<passphrase>" -Dgpg.keyname="<your key ID>"
                                        In order to create the key pair, use the command "gpg &ndash;&ndash;gen-key".
                                        (&ndash;&ndash; stands for double dash)
                                        -->
                                        <artifactId>maven-gpg-plugin</artifactId>
                                        <version>1.5</version>
                                        <executions>
                                            <execution>
                                                <id>gpg-sign</id>
                                                <phase>verify</phase>
                                                <goals>
                                                    <goal>sign</goal>
                                                </goals>
                                            </execution>
                                        </executions>
                                    </plugin>
                                </plugins>
                            </build>
                        </profile>
                        <profile>
                            <id>generate-docs</id>
                            <!--
                            Generate the documentation artifacts. 
                            Note: this profile is also required to be active for release
                            builds due to the packaging requirements of the Central repo
                            -->
                            <build>
                                <plugins>
                                    <plugin>
                                        <artifactId>maven-source-plugin</artifactId>
                                        <executions>
                                            <execution>
                                                <id>attach-sources</id>
                                                <phase>prepare-package</phase>
                                                <goals>
                                                    <goal>jar-no-fork</goal>
                                                </goals>
                                            </execution>
                                        </executions>
                                    </plugin>
                                    <plugin>
                                        <artifactId>maven-javadoc-plugin</artifactId>
                                        <executions>
                                            <execution>
                                                <id>attach-javadoc</id>
                                                <phase>package</phase>
                                                <goals>
                                                    <goal>jar</goal>
                                                </goals>
                                            </execution>
                                        </executions>
                                    </plugin>
                                </plugins>
                            </build>
                        </profile>
                        <profile>
                            <id>restrict-doclint</id>
                            <!-- doclint is only supported by JDK 8 -->
                            <activation>
                                <jdk>[1.8,)</jdk>
                            </activation>
                            <build>
                                <plugins>
                                    <plugin>
                                        <artifactId>maven-compiler-plugin</artifactId>
                                        <configuration>
                                            <compilerArgs>
                                                <arg>-Xlint:unchecked</arg>
                                                <arg>-Xdoclint:accessibility,reference,syntax</arg>
                                            </compilerArgs>
                                        </configuration>
                                    </plugin>
                                    <plugin>
                                        <artifactId>maven-javadoc-plugin</artifactId>
                                        <configuration>
                                            <additionalparam>-Xdoclint:accessibility -Xdoclint:reference</additionalparam>
                                        </configuration>
                                    </plugin>
                                </plugins>
                            </build>
                            <reporting>
                                <plugins>
                                    <plugin>
                                        <artifactId>maven-javadoc-plugin</artifactId>
                                        <configuration>
                                            <additionalparam>-Xdoclint:accessibility -Xdoclint:reference</additionalparam>
                                        </configuration>
                                    </plugin>
                                </plugins>
                            </reporting>
                        </profile>
                        <profile>
                            <id>fast-tests</id>
                            <build>
                                <plugins>
                                    <plugin>
                                        <artifactId>maven-surefire-plugin</artifactId>
                                        <configuration>
                                            <parallel>classes</parallel>
                                            <threadCountClasses>2</threadCountClasses>
                                        </configuration>
                                        <dependencies>
                                            <dependency>
                                                <groupId>org.apache.maven.surefire</groupId>
                                                <artifactId>surefire-junit47</artifactId>
                                                <version>2.17</version>
                                            </dependency>
                                        </dependencies>
                                    </plugin>
                                </plugins>
                            </build>
                        </profile>
                    </profiles>
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