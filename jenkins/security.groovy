#!groovy
 
import jenkins.model.*
import hudson.security.*
import hudson.security.csrf.DefaultCrumbIssuer
import jenkins.security.s2m.AdminWhitelistRule
 
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

