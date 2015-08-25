package com.megatome.grails.recaptcha

import com.megatome.grails.recaptcha.net.Post
import com.megatome.grails.recaptcha.net.QueryParams
import groovy.json.JsonSlurper
import groovy.mock.interceptor.StubFor
import spock.lang.Specification

/**
 * Copyright 2010-2015 Megatome Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class ReCaptchaTest extends Specification {
    private ReCaptcha r;

    def setup() {
        r = new ReCaptcha(privateKey: "testing", publicKey: "testing", includeNoScript: false)
    }

    def "Create captcha with various script options"() {
        when:"includeScript is false"
        r.includeScript = false

        then:"No script tag should be emitted in the captcha unless specified in the options"
        !r.createRecaptchaHtml(null).contains("<script")
        r.createRecaptchaHtml(includeScript: true).contains("<script")

        when:"includeScript is true"
        r.includeScript = true

        then:"Script tag should be emitted as part of the captcha"
        r.createRecaptchaHtml(includeScript: true).contains("<script")

        when:"includeNoScript is true"
        r.includeNoScript = true

        then:"noscript tag should be emitted as part of the captcha"
        r.createRecaptchaHtml(null).contains("<noscript>")
    }

    def "Create captcha with options"() {
        when:"No options are specified"
        def html = r.createRecaptchaHtml(null)

        then:
        html.contains("g-recaptcha")
        html.contains("data-sitekey")
        !html.contains("data-theme")
        !html.contains("data-tabindex")
        !html.contains("data-type")
        !html.contains("data-callback")
        !html.contains("data-expired-callback")

        when:"Theme is specified"
        html = r.createRecaptchaHtml(theme: "mytheme")

        then:
        html.contains("data-theme=\"mytheme\"")

        when:"Tabindex is specified"
        html = r.createRecaptchaHtml(tabindex: "0")

        then:
        html.contains("data-tabindex=\"0\"")

        when:"Type is specified"
        html = r.createRecaptchaHtml(type: "image")

        then:
        html.contains("data-type=\"image\"")

        when:"successCallback is specified"
        html = r.createRecaptchaHtml(successCallback: "foo")

        then:
        html.contains("data-callback=\"foo\"")

        when:"expiredCallback is specified"
        html = r.createRecaptchaHtml(expiredCallback: "foo")

        then:
        html.contains("data-expired-callback=\"foo\"")

        when:"lang is specified"
        html = r.createRecaptchaHtml(lang: "fr")

        then:
        html.contains("hl=fr")
    }

    def "Create explicit captcha"() {
        expect:
        buildAndCheckExplicitHTML(loadCallback: "foo")

        when:
        r.includeNoScript = true

        then:
        buildAndCheckExplicitHTML(loadCallback: "foo", true)
    }

    def "Create explicit captcha with no callback"() {
        when:
        r.createRecaptchaExplicitHtml(null)

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.contains("loadCallback")
    }

    def "Create explicit captcha with lang parameter"() {
        expect:
        buildAndCheckExplicitHTML(loadCallback: "foo", lang: "fr")
    }

    def "Create render parameters"() {
        when:
        def json = r.createRenderParameters(null)

        then:
        json.contains("sitekey")
        !json.contains("theme")
        !json.contains("tabindex")
        !json.contains("type")
        !json.contains("callback")
        !json.contains("expired-callback")

        when:"Theme is specified"
        json = r.createRenderParameters(theme: "mytheme")

        then:
        json.contains("'theme': 'mytheme'")

        when:"Tabindex is specified"
        json = r.createRenderParameters(tabindex: "0")

        then:
        json.contains("'tabindex': '0'")

        when:"Type is specified"
        json = r.createRenderParameters(type: "image")

        then:
        json.contains("'type': 'image'")

        when:"successCallback is specified"
        json = r.createRenderParameters(successCallback: "foo")

        then:
        json.contains("'callback': 'foo'")

        when:"expiredCallback is specified"
        json = r.createRenderParameters(expiredCallback: "foo")

        then:
        json.contains("'expired-callback': 'foo'")
    }

    def "Verify answer validation"() {
        when:"A sucessful response message"
        def answer = """{ "success": true }"""

        then:
        buildAndCheckAnswer(answer, true)

        when:"A failure response message"
        answer = """{ "success": false }"""

        then:
        buildAndCheckAnswer(answer, false)

        when:"An invalid response message"
        answer = """{ "foo": "bar" }"""

        then:
        buildAndCheckAnswer(answer, false)

        when:"An answer response with messages"
        answer = """{
            "success": false,
            "error-codes": ["missing-input-response"]
            }"""

        then:
        buildAndCheckAnswer(answer, false)

        when:"A null response"
        answer = null

        then:
        buildAndCheckAnswer(answer, false)
    }

    private void buildAndCheckAnswer(String postText, boolean expectedValid) {
        def stub = new StubFor(Post.class)
        stub.demand.hasProperty(3..3) { true }
        stub.demand.setUrl() {}
        stub.demand.setProxy() {}
        stub.demand.getQueryParams(3..3) { new QueryParams(null) }
        stub.demand.getResponse() { postText == null ? null : new JsonSlurper().parseText(postText) }

        stub.use {
            def response = r.checkAnswer("123.123.123.123", "response")

            assertTrue response == expectedValid
        }
    }

    private void buildAndCheckExplicitHTML(Map options, includeNoScript = false) {
        def expectedLang = null
        if (options.lang) {
            expectedLang = options.lang
        }
        def html = r.createRecaptchaExplicitHtml(options)
        assert html.contains("render=explicit")
        if (expectedLang) {
            assert html.contains("hl=${expectedLang}")
        } else {
            assert !html.contains("hl=")
        }
        assert html.contains("onload=" + options.loadCallback)
        if (includeNoScript) {
            assert html.contains("<noscript>")
        }
    }
}