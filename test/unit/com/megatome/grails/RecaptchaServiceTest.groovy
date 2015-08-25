package com.megatome.grails

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(RecaptchaService)
class RecaptchaServiceTest extends Specification {

    void "load with no config"() {
        when:
        service.isEnabled()

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.contains("configuration not specified")
    }

    void "load with empty public key"() {
        setup:
        config.recaptcha.publicKey = ""
        config.recaptcha.privateKey = ""

        when:
        service.isEnabled()

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.contains("Public Key must be specified")
    }

    void "load with empty private key"() {
        setup:
        config.recaptcha.publicKey = "ABC"
        config.recaptcha.privateKey = ""

        when:
        service.isEnabled()

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.contains("Private Key must be specified")
    }

    void "test create regular captcha"() {
        setup:
        config.recaptcha.publicKey = "ABC"
        config.recaptcha.privateKey = "123"
        config.recaptcha.includeNoScript = true

        when:
        def response = service.createCaptcha([:])

        then:
        response.contains("\"g-recaptcha\"")
        response.contains("data-sitekey=\"ABC\"")
        response.contains("<noscript>")
    }

    void "test create regular captcha with proxy"() {
        setup:
        config.recaptcha.publicKey = "ABC"
        config.recaptcha.privateKey = "123"
        config.recaptcha.proxy.server = "localhost"
        config.recaptcha.proxy.port = "8080"
        config.recaptcha.proxy.username = "foo"
        config.recaptcha.proxy.password = "bar"

        when:
        def response = service.createCaptcha([:])

        then:
        response.contains("\"g-recaptcha\"")
        response.contains("data-sitekey=\"ABC\"")
        response.contains("<noscript>")
    }

    void "test create explicit captcha"() {
        setup:
        config.recaptcha.publicKey = "ABC"
        config.recaptcha.privateKey = "123"

        when:
        def response = service.createCaptchaExplicit([loadCallback: "loadCB"])

        then:
        response.contains("loadCB")
        !response.contains("\"g-recaptcha\"")
        !response.contains("data-sitekey=\"ABC\"")
        response.contains("<noscript>")
    }
}
