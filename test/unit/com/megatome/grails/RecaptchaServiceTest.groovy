package com.megatome.grails

import grails.test.mixin.TestFor
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
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

        when:
        response = service.createCaptcha(theme:"dark", lang:"fr", type:"audio", size: "normal", successCallback: "successCB", expiredCallback: "expiredCB", tabindex: 1, includeScript: true)

        then:
        response.contains("\"g-recaptcha\"")
        response.contains("data-sitekey=\"ABC\"")
        response.contains("<noscript>")
        response.contains("data-theme=\"dark\"")
        response.contains("data-type=\"audio\"")
        response.contains("data-size=\"normal\"")
        response.contains("data-callback=\"successCB\"")
        response.contains("data-expired-callback=\"expiredCB\"")
        response.contains("data-tabindex=\"1\"")
        response.contains("<script")
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

    void "test create render parameters"() {
        setup:
        def slurper = new JsonSlurper()
        slurper.type = JsonParserType.LAX
        config.recaptcha.publicKey = "ABC"
        config.recaptcha.privateKey = "123"

        when:
        def response = service.createRenderParameters(null)
        def json = slurper.parseText(response)

        then:
        json.sitekey == "ABC"

        when:
        response = service.createRenderParameters(theme:"dark", type:"audio", size: "normal", successCallback: "successCB", expiredCallback: "expiredCB", tabindex: 1)
        json = slurper.parseText(response)

        then:
        json.theme == "dark"
        json.type == "audio"
        json.size == "normal"
        json.callback == "successCB"
        json["expired-callback"] == "expiredCB"
        json.tabindex == "1"

        when:
        response = service.createRenderParameters(theme:"dark", lang:"fr", foo:"bar")
        json = slurper.parseText(response)

        then:
        json.theme == "dark"
        !json.containsKey("lang")
        !json.containsKey('foo')
    }
}
