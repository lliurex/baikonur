#!groovy

import jenkins.* 
import jenkins.model.*
import hudson.*
import hudson.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import hudson.slaves.*
import hudson.plugins.sshslaves.verifiers.NonVerifyingKeyVerificationStrategy
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry
import jenkins.security.s2m.AdminWhitelistRule
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import org.jenkinsci.plugins.strictcrumbissuer.*

def instance = Jenkins.getInstance()

// Create admin user

def user = new File("/run/secrets/jenkins-user").text.trim()
def pass = new File("/run/secrets/jenkins-pass").text.trim()

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(user, pass)
instance.setSecurityRealm(hudsonRealm)
 
def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
instance.setAuthorizationStrategy(strategy)
instance.save()
 
Jenkins.instance.getInjector().getInstance(AdminWhitelistRule.class).setMasterKillSwitch(false)

// Enable crumb

if(!Jenkins.instance.isQuietingDown()) {
    def j = Jenkins.instance
    if(j.getCrumbIssuer() == null) {
        def sci = new StrictCrumbIssuer()
        sci.setCheckSessionMatch(false)
        j.setCrumbIssuer(sci)
        j.save()
        println 'CSRF Protection configuration has changed.  Enabled CSRF Protection.'
    }
    else {
        println 'Nothing changed.  CSRF Protection already configured.'
    }
}
else {
    println "Shutdown mode enabled.  Configure CSRF protection SKIPPED."
}

// Set url path

def jenkinsLocationConfiguration = JenkinsLocationConfiguration.get()
jenkinsLocationConfiguration.setUrl('http://jenkins_service:8080/')


// Disable cli

//jenkins.CLI.get().setEnabled(false)


// Create jobs

def jobName = "autobuild"

def file_config = new File("/usr/share/baikonur/autobuild/config.xml")
def template_text = file_config.text
def engine = new groovy.text.SimpleTemplateEngine()
def binding = ["numberbuilder":"1"]
def template = engine.createTemplate(template_text).make(binding)
def xmlStream = new ByteArrayInputStream( template.toString().getBytes() )

Jenkins.instance.createProjectFromXML(jobName, xmlStream)

for (i=1;i<=4;i++){
    def jobNameNum = jobName + "_" + i
    binding = ["numberbuilder":i.toString()]
    xmlStream = new ByteArrayInputStream( engine.createTemplate(template_text).make(binding).toString().getBytes() )
    Jenkins.instance.createProjectFromXML(jobNameNum, xmlStream)
}


jobName = "repoautobuild"

file_config = new File("/usr/share/baikonur/repobuild/config.xml")
xmlStream = new ByteArrayInputStream( file_config.getBytes() )

Jenkins.instance.createProjectFromXML(jobName, xmlStream)

// Create credentials

// Username and password
Credentials c = (Credentials) new UsernamePasswordCredentialsImpl(
CredentialsScope.GLOBAL, // Scope
"sbuild_credentials", // id
"Credentials to sbuild", // description
user, // username
pass // password
)

SystemCredentialsProvider.getInstance().getStore().addCredentials(Domain.global(), c)


// Create Nodes 

ComputerLauncher launcher = new hudson.plugins.sshslaves.SSHLauncher(
            "sbuild_1", 
            22, 
            "sbuild_credentials", 
            (String)null, 
            "/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java", 
            (String)null, 
            (String)null, 
            210, 
            10, 
            15, 
            new NonVerifyingKeyVerificationStrategy())


Slave agent = new DumbSlave("sbuild_1", "/home/"+user, launcher)
agent.nodeDescription = "Sbuild agent"
agent.numExecutors = 1
agent.labelString = "sbuild_1"
agent.mode = Node.Mode.NORMAL
agent.retentionStrategy = new RetentionStrategy.Always()

// List<Entry> env = new ArrayList<Entry>();
// env.add(new Entry("key1","value1"))
// env.add(new Entry("key2","value2"))
// EnvironmentVariablesNodeProperty envPro = new EnvironmentVariablesNodeProperty(env);

// agent.getNodeProperties().add(envPro)

// Create a "Permanent Agent"
Jenkins.instance.addNode(agent)

launcher = new hudson.plugins.sshslaves.SSHLauncher(
            "sbuild_2", 
            22, 
            "sbuild_credentials", 
            (String)null, 
            "/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java", 
            (String)null, 
            (String)null, 
            210, 
            10, 
            15, 
            new NonVerifyingKeyVerificationStrategy())

agent = new DumbSlave("sbuild_2", "/home/"+user, launcher)
agent.nodeDescription = "Sbuild agent"
agent.numExecutors = 1
agent.labelString = "sbuild_2"
agent.mode = Node.Mode.NORMAL
agent.retentionStrategy = new RetentionStrategy.Always()

Jenkins.instance.addNode(agent)

launcher = new hudson.plugins.sshslaves.SSHLauncher(
            "sbuild_3", 
            22, 
            "sbuild_credentials", 
            (String)null, 
            "/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java", 
            (String)null, 
            (String)null, 
            210, 
            10, 
            15, 
            new NonVerifyingKeyVerificationStrategy())

agent = new DumbSlave("sbuild_3", "/home/"+user, launcher)
agent.nodeDescription = "Sbuild agent"
agent.numExecutors = 1
agent.labelString = "sbuild_3"
agent.mode = Node.Mode.NORMAL
agent.retentionStrategy = new RetentionStrategy.Always()

Jenkins.instance.addNode(agent)

launcher = new hudson.plugins.sshslaves.SSHLauncher(
            "sbuild_4", 
            22, 
            "sbuild_credentials", 
            (String)null, 
            "/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java", 
            (String)null, 
            (String)null, 
            210, 
            10, 
            15, 
            new NonVerifyingKeyVerificationStrategy())

agent = new DumbSlave("sbuild_4", "/home/"+user, launcher)
agent.nodeDescription = "Sbuild agent"
agent.numExecutors = 1
agent.labelString = "sbuild_4"
agent.mode = Node.Mode.NORMAL
agent.retentionStrategy = new RetentionStrategy.Always()

Jenkins.instance.addNode(agent)


launcher = new hudson.plugins.sshslaves.SSHLauncher(
            "repobuilder_service", 
            22, 
            "sbuild_credentials", 
            (String)null, 
            "/usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java", 
            (String)null, 
            (String)null, 
            210, 
            10, 
            15, 
            new NonVerifyingKeyVerificationStrategy())

agent = new DumbSlave("repobuilder_service", "/home/"+user, launcher)
agent.nodeDescription = "Sbuild agent"
agent.numExecutors = 1
agent.labelString = "repobuilder_service"
agent.mode = Node.Mode.NORMAL
agent.retentionStrategy = new RetentionStrategy.Always()

Jenkins.instance.addNode(agent)

