
//
// This script is executed by Grails after plugin was installed to project.
// This script is a Gant script so you can use all special variables provided
// by Gant (such as 'baseDir' which points on project base dir). You can
// use 'Ant' to access a global instance of AntBuilder
//
//
if (!ant.available(file: "${basedir}/grails-app/conf/RecaptchaConfig.groovy")) {

    ant.copy(file:"${pluginBasedir}/src/templates/RecaptchaConfig.groovy",
             todir:"${basedir}/grails-app/conf")
}

ant.property(environment:"env")
grailsHome = ant.antProject.properties."env.GRAILS_HOME"

