package com.megatome.grails

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(RecaptchaTagLib)
class RecaptchaTagLibTest extends Specification {
    void "test recaptcha tag with no attributes"() {
        given:
        def recapMock = mockFor(RecaptchaService)
        def params
        recapMock.demand.createCaptcha { Map props -> params = props; return "" }
        tagLib.recaptchaService = recapMock.createMock()

        expect:
        tagLib.recaptcha() == ""
        params != null
        params == [:]
    }

    void "test recaptcha tag with all attributes"() {
        given:
        def recapMock = mockFor(RecaptchaService)
        def params
        recapMock.demand.createCaptcha { Map props -> params = props; return "" }
        tagLib.recaptchaService = recapMock.createMock()

        expect:
        tagLib.recaptcha(theme:"dark", lang:"fr", successCallback: "successCB", expiredCallback: "expiredCB", tabindex: 1, includeScript: true) == ""
        params != null
        params.theme == "dark"
        params.lang == "fr"
        params.successCallback == "successCB"
        params.expiredCallback == "expiredCB"
        params.tabindex == 1
        params.includeScript == true
    }

    void "test recaptcha tag with illegal attributes"() {
        given:
        def recapMock = mockFor(RecaptchaService)
        def params
        recapMock.demand.createCaptcha { Map props -> params = props; return "" }
        tagLib.recaptchaService = recapMock.createMock()

        expect:
        tagLib.recaptcha(theme:"dark", foo:"bar") == ""
        params != null
        params.theme == "dark"
        !params.containsKey('foo')
    }
}
