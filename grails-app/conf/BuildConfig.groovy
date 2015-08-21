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
    dependencies {
        // Latest httpcore and httpmime for Coveralls plugin
        build 'org.apache.httpcomponents:httpcore:4.3.2'
        build 'org.apache.httpcomponents:httpclient:4.3.2'
        build 'org.apache.httpcomponents:httpmime:4.3.3'
        test 'org.springframework:spring-expression:4.0.9.RELEASE'
        test 'org.springframework:spring-aop:4.0.9.RELEASE'
    }

    plugins {
        // Coveralls plugin
        build(':coveralls:0.1.3', ':rest-client-builder:1.0.3', ':release:3.0.1') {
            export = false
        }
        test(':code-coverage:2.0.3-3') {
            export = false
        }
    }
}