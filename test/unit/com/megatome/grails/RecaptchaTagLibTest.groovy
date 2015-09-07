package com.megatome.grails

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(RecaptchaTagLib)
class RecaptchaTagLibTest extends Specification {
    void "test recaptcha tag"() {
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
        tagLib.recaptcha(theme:"dark", lang:"fr", type:"audio", size: "normal", successCallback: "successCB", expiredCallback: "expiredCB", tabindex: 1, includeScript: true)

        then:
        params != null
        params.theme == "dark"
        params.lang == "fr"
        params.type == "audio"
        params.size == "normal"
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

    void "test recaptchaExplicit tag"() {
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

    void "test renderParameters tag"() {
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
        tagLib.renderParameters(theme:"dark", type:"audio", size: "normal", successCallback: "successCB", expiredCallback: "expiredCB", tabindex: 1)

        then:
        params != null
        params.theme == "dark"
        params.size == "normal"
        params.successCallback == "successCB"
        params.expiredCallback == "expiredCB"
        params.tabindex == 1

        when:
        tagLib.renderParameters(theme:"dark", lang:"fr", foo:"bar")

        then:
        params != null
        params.theme == "dark"
        !params.containsKey("lang")
        !params.containsKey('foo')
    }

    void "test script tag"() {
        setup:
        def recapMock = mockFor(RecaptchaService)
        def params = [:]
        recapMock.demand.createScriptEntry(3..3) { Map props -> params = props; return "" }
        tagLib.recaptchaService = recapMock.createMock()

        when:
        tagLib.script()

        then:
        params != null
        params == [:]

        when:
        tagLib.script(lang:"fr")

        then:
        params != null
        params.lang == "fr"

        when:
        tagLib.script(lang:"fr", foo:"bar")

        then:
        params != null
        params.lang == "fr"
        !params.containsKey('foo')
    }

    void "test ifEnabled(true)/isDisabled(false) tag"() {
        setup:
        def recapMock = mockFor(RecaptchaService)
        recapMock.demand.isEnabled(2..2) { -> true }
        tagLib.recaptchaService = recapMock.createMock()

        when:
        def response = tagLib.ifEnabled(null, { "Enabled" })

        then:
        response.toString() == "Enabled"

        when:
        response = tagLib.ifDisabled(null, { "Disabled" })

        then:
        response.toString() == ""
    }

    void "test ifEnabled(false)/isDisabled(true) tag"() {
        setup:
        def recapMock = mockFor(RecaptchaService)
        recapMock.demand.isEnabled(2..2) { -> false }
        tagLib.recaptchaService = recapMock.createMock()

        when:
        def response = tagLib.ifEnabled(null, { "Enabled" })

        then:
        response.toString() == ""

        when:
        response = tagLib.ifDisabled(null, { "Disabled" })

        then:
        response.toString() == "Disabled"
    }

    void "test ifFailed(true) tag"() {
        setup:
        def recapMock = mockFor(RecaptchaService)
        recapMock.demand.validationFailed { session -> true }
        tagLib.recaptchaService = recapMock.createMock()

        when:
        def response = tagLib.ifFailed(null, { "Failed" })

        then:
        response.toString() == "Failed"
    }

    void "test ifFailed(false) tag"() {
        setup:
        def recapMock = mockFor(RecaptchaService)
        recapMock.demand.validationFailed { session -> false }
        tagLib.recaptchaService = recapMock.createMock()

        when:
        def response = tagLib.ifFailed(null, { "Success" })

        then:
        response.toString() == ""
    }
}
