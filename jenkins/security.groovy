#!groovy
 
import jenkins.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import hudson.slaves.*
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry
import jenkins.security.s2m.AdminWhitelistRule
import com.cloudbees.plugins.credentials.impl.*;
import com.cloudbees.plugins.credentials.*;
import org.jenkinsci.plugins.plaincredentials.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import com.cloudbees.plugins.credentials.common.*
 
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
        j.setCrumbIssuer(new DefaultCrumbIssuer(true))
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

jenkins.CLI.get().setEnabled(false)


// Create jobs

def jobName = "autobuild"

def file_config = new File("/usr/share/baikonur/autobuild/config.xml")
def xmlStream = new ByteArrayInputStream( file_config.getBytes() )

Jenkins.instance.createProjectFromXML(jobName, xmlStream)

for (i=1;i<=4;i++){
    def jobNameNum = jobName + "_" + i
    file_config = new File("/usr/share/baikonur/autobuild/config.xml")
    xmlStream = new ByteArrayInputStream( file_config.getBytes() )
    Jenkins.instance.createProjectFromXML(jobNameNum, xmlStream)
}


jobName = "repoautobuild"

file_config = new File("/usr/share/baikonur/repobuild/config.xml")
xmlStream = new ByteArrayInputStream( file_config.getBytes() )

Jenkins.instance.createProjectFromXML(jobName, xmlStream)

// Create credentials

/ Username and password
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
        "host", // Host
        22, // Port
        "sbuild_credentials", // Credentials
        (String)null, // JVM Options
        (String)null, // JavaPath
        (String)null, // Prefix Start Slave Command
        (String)null, // Suffix Start Slave Command
        (Integer)null, // Connection Timeout in Seconds
        (Integer)null, // Maximum Number of Retries
        (Integer)null // The number of seconds to wait between retries
)


Slave agent = new DumbSlave("agent-node", "/home/jenkins", launcher)
agent.nodeDescription = "Agent node description"
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
