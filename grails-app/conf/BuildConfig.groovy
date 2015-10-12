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
        provided "org.springframework:spring-expression:$springVersion"
        provided "org.springframework:spring-aop:$springVersion"
        test 'org.hamcrest:hamcrest-core:1.3'
    }

    plugins {
        // Coveralls plugin
        build(':coveralls:0.1.3', ':release:3.1.1') {
            export = false
        }
        compile(':rest-client-builder:2.1.1')
        test(':code-coverage:2.0.3-3') {
            export = false
        }
    }
}