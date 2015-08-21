package com.megatome.grails

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(RecaptchaTagLib)
class RecaptchaTagLibTest extends Specification {
    def recapMock
    def params

    /*def setup() {
        recapMock = mockFor(RecaptchaService)
        params = [:]
        recapMock.demand.createCaptcha { Map props -> params = props; return "" }
        recapMock.demand.createCaptchaExplicit { Map props -> params = props; return "" }
        tagLib.recaptchaService = recapMock.createMock()
    }*/

    def setupMock(explicit) {
        recapMock = mockFor(RecaptchaService)
        params = [:]
        if (!explicit) {
            recapMock.demand.createCaptcha { Map props -> params = props; return "" }
        } else {
            recapMock.demand.createCaptchaExplicit { Map props -> params = props; return "" }
        }
        tagLib.recaptchaService = recapMock.createMock()
    }

    void "test recaptcha tag with no attributes"() {
        setup:
        setupMock(false)

        when:
        tagLib.recaptcha()

        then:
        params != null
        params == [:]
    }

    void "test recaptcha tag with all attributes"() {
        setup:
        setupMock(false)

        when:
        tagLib.recaptcha(theme:"dark", lang:"fr", successCallback: "successCB", expiredCallback: "expiredCB", tabindex: 1, includeScript: true)

        then:
        params != null
        params.theme == "dark"
        params.lang == "fr"
        params.successCallback == "successCB"
        params.expiredCallback == "expiredCB"
        params.tabindex == 1
        params.includeScript == true
    }

    void "test recaptcha tag with illegal attributes"() {
        setup:
        setupMock(false)

        when:
        tagLib.recaptcha(theme:"dark", foo:"bar")

        then:
        params != null
        params.theme == "dark"
        !params.containsKey('foo')
    }

    void "test recaptchaExplicit tag with no attributes"() {
        setup:
        setupMock(true)

        when:
        tagLib.recaptchaExplicit()

        then:
        params != null
        params == [:]
    }

    void "test recaptchaExplicit tag with all attributes"() {
        setup:
        setupMock(true)

        when:
        tagLib.recaptchaExplicit(lang:"fr", loadCallback: "loadCB")

        then:
        params != null
        params.lang == "fr"
        params.loadCallback == "loadCB"
    }

    void "test recaptchaExplicit tag with illegal attributes"() {
        setup:
        setupMock(true)

        when:
        tagLib.recaptchaExplicit(lang:"fr", foo:"bar")

        then:
        params != null
        params.lang == "fr"
        !params.containsKey('foo')
    }
}
