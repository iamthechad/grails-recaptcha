package com.megatome.grails

import com.megatome.grails.recaptcha.ReCaptcha
import grails.test.mixin.TestFor
import grails.test.runtime.FreshRuntime
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import groovy.mock.interceptor.StubFor
import spock.lang.Specification

@FreshRuntime
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

    void "test script entry"() {
        setup:
        config.recaptcha.publicKey = "ABC"
        config.recaptcha.privateKey = "123"

        when:
        def response = service.createScriptEntry(null)

        then:
        response.contains("<script")
        response.contains("async defer")
        !response.contains("hl=")

        when:
        response = service.createScriptEntry(lang:"fr")

        then:
        response.contains("hl=fr")

        when:
        response = service.createScriptEntry(lang:"fr", foo:"bar")

        then:
        response.contains("hl=fr")
        !response.contains('foo')
    }

    void "test verify answer when captcha is not enabled"() {
        setup:
        config.recaptcha.publicKey = "ABC"
        config.recaptcha.privateKey = "123"
        config.recaptcha.enabled = false

        expect:
        service.verifyAnswer([:], "127.0.0.1", [:])
    }

    void "test verify answer true"() {
        setup:
        config.recaptcha.publicKey = "ABC"
        config.recaptcha.privateKey = "123"

        when:
        def recapStub = new StubFor(ReCaptcha.class)
        recapStub.demand.checkAnswer { remoteAddr, response -> true }
        def session = [:]

        then:
        recapStub.use {
            service.verifyAnswer(session, "127.0.0.1", ["g-recaptcha-response": "foo"])
            session["recaptcha_error"] == true
        }
    }

    void "test verify answer false"() {
        setup:
        config.recaptcha.publicKey = "ABC"
        config.recaptcha.privateKey = "123"
        def session = [:]

        when:
        def recapStub = new StubFor(ReCaptcha.class)
        recapStub.demand.checkAnswer { remoteAddr, response -> false }

        then:
        recapStub.use {
            !service.verifyAnswer(session, "127.0.0.1", ["g-recaptcha-response": "foo"])
            !session.containsKey("recaptcha_error")
        }
    }

    void "test verify check validation failed in session"() {
        setup:
        def session = [:]

        expect:
        !service.validationFailed(session)

        when:
        session["recaptcha_error"] = "true"

        then:
        service.validationFailed(session)
    }

    void "test verify clean up session"() {
        setup:
        def session = [recaptcha_error: "true", other_data: "foo"]

        when:
        service.cleanUp(session)

        then:
        session["recaptcha_error"] == null
        session["other_data"] == "foo"
    }
}
