node {
    def mvnHome
    def jdk
    def server
    def buildInfo
    def rtMaven

    stage('Setup') {
        mvnHome = tool 'Maven'
        jdk = tool name: 'Java 21'
        env.JAVA_HOME = "${jdk}"

        server = Artifactory.server 'Artifactory'

        rtMaven = Artifactory.newMavenBuild()
        rtMaven.tool = 'Maven'
        rtMaven.deployer releaseRepo: 'rahmnathan-libraries', snapshotRepo: 'rahmnathan-libraries', server: server

        buildInfo = Artifactory.newBuildInfo()
    }
    stage('Checkout') {
        git 'https://github.com/rahmnathan/oauth2-adapter.git'
    }
    stage('Set Version') {
        PROJECT_VERSION = sh(
                script: "'${mvnHome}/bin/mvn' help:evaluate -Dexpression=project.version -q -DforceStdout",
                returnStdout: true
        ).trim()
        env.NEW_VERSION = "${PROJECT_VERSION}.${BUILD_NUMBER}"
        sh "'${mvnHome}/bin/mvn' -DnewVersion='${NEW_VERSION}' versions:set"
    }
    stage('Tag') {
        sh 'git config --global user.email "rahm.nathan@gmail.com"'
        sh 'git config --global user.name "rahmnathan"'
        sshagent(credentials: ['Github-Git']) {
            sh 'mkdir -p /home/jenkins/.ssh'
            sh 'ssh-keyscan  github.com >> ~/.ssh/known_hosts'
            sh "'${mvnHome}/bin/mvn' -Dtag=${NEW_VERSION} scm:tag"
        }
    }
    stage('Test') {
        sh "'${mvnHome}/bin/mvn' test"
    }
    stage('Package & Deploy') {
        rtMaven.run pom: 'pom.xml', goals: 'clean install -DskipTests', buildInfo: buildInfo
    }
}