package com.megatome.grails

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(RecaptchaTagLib)
class RecaptchaTagLibTest extends Specification {
    void "test recaptcha tag with no attributes"() {
        setup:
        def recapMock = mockFor(RecaptchaService)
        def params = [:]
        recapMock.demand.createCaptcha(3..3) { Map props -> params = props; return "" }
        tagLib.recaptchaService = recapMock.createMock()

        when:
        tagLib.recaptcha()

        then:
        params != null
        params == [:]

        when:
        tagLib.recaptcha(theme:"dark", lang:"fr", type:"audio", successCallback: "successCB", expiredCallback: "expiredCB", tabindex: 1, includeScript: true)

        then:
        params != null
        params.theme == "dark"
        params.lang == "fr"
        params.type == "audio"
        params.successCallback == "successCB"
        params.expiredCallback == "expiredCB"
        params.tabindex == 1
        params.includeScript == true

        when:
        tagLib.recaptcha(theme:"dark", foo:"bar")

        then:
        params != null
        params.theme == "dark"
        !params.containsKey('foo')
    }

    void "test recaptchaExplicit tag with no attributes"() {
        setup:
        def recapMock = mockFor(RecaptchaService)
        def params = [:]
        recapMock.demand.createCaptchaExplicit(3..3) { Map props -> params = props; return "" }
        tagLib.recaptchaService = recapMock.createMock()

        when:
        tagLib.recaptchaExplicit()

        then:
        params != null
        params == [:]

        when:
        tagLib.recaptchaExplicit(lang:"fr", loadCallback: "loadCB")

        then:
        params != null
        params.lang == "fr"
        params.loadCallback == "loadCB"

        when:
        tagLib.recaptchaExplicit(lang:"fr", foo:"bar")

        then:
        params != null
        params.lang == "fr"
        !params.containsKey('foo')
    }

    void "test renderParametrs tag"() {
        setup:
        def recapMock = mockFor(RecaptchaService)
        def params = [:]
        recapMock.demand.createRenderParameters(3..3) { Map props -> params = props; return "" }
        tagLib.recaptchaService = recapMock.createMock()

        when:
        tagLib.renderParameters()

        then:
        params != null
        params == [:]

        when:
        tagLib.renderParameters(theme:"dark", lang:"fr", type:"audio", successCallback: "successCB", expiredCallback: "expiredCB", tabindex: 1)

        then:
        params != null
        params.theme == "dark"
        params.lang == "fr"
        params.successCallback == "successCB"
        params.expiredCallback == "expiredCB"
        params.tabindex == 1

        when:
        tagLib.renderParameters(theme:"dark", foo:"bar")

        then:
        params != null
        params.theme == "dark"
        !params.containsKey('foo')
    }
}
