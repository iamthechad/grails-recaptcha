grails.project.source.level = 1.6

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenCentral()
    }

    plugins {
        build(':release:2.0.3') {
            export = false
        }
    }
}