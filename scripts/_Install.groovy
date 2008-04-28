
//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'Ant' to access a global instance of AntBuilder
//
//
Ant.copy(file:"${pluginBasedir}/src/templates/RecaptchaConfig.groovy",
         todir:"${basedir}/grails-app/conf")

Ant.property(environment:"env")
grailsHome = Ant.antProject.properties."env.GRAILS_HOME"

