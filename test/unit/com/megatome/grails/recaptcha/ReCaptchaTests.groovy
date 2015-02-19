package com.megatome.grails.recaptcha

import com.megatome.grails.recaptcha.net.Post
import groovy.mock.interceptor.MockFor
import com.megatome.grails.recaptcha.net.QueryString
import groovy.json.JsonSlurper

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

public class ReCaptchaTests extends GroovyTestCase {
    private ReCaptcha r;

    protected void setUp() throws Exception {
        r = new ReCaptcha(privateKey: "testing", publicKey: "testing", includeNoScript: false)
    }

    public void testCreateCaptchaHtml() {
        r.includeScript = false
        assertFalse r.createRecaptchaHtml(null).contains("<script")

        r.includeScript = true
        assertTrue r.createRecaptchaHtml(null).contains("<script")

        r.includeNoScript = true
        assertTrue r.createRecaptchaHtml(null).contains("<noscript>")

        def options = new Properties()
        options.setProperty("theme", "mytheme")
        def html = r.createRecaptchaHtml(options)
        assertTrue html.contains("data-theme=\"mytheme\"")
    }

    public void testCreateCaptchaHtmlWithLangInOptions() {
        def options = [:]
        options.lang = "fr"
        def html = r.createRecaptchaHtml(options)
        assertTrue html.contains("<script")
        assertTrue html.contains("hl=fr")

        options.lang = null
        html = r.createRecaptchaHtml(options)
        assertTrue html.contains("<script")
        assertFalse html.contains("hl=fr")
    }

    public void testCheckAnswerSuccess() {
        def answer = """{ "success": true }"""
        buildAndCheckAnswer(answer, true)
    }

    public void testCheckAnswerFail() {
        def answer = """{ "success": false }"""
        buildAndCheckAnswer(answer, false)
    }

    public void testCheckAnswerInvalidResponse() {
        def answer = """{ "foo": "bar" }"""
        buildAndCheckAnswer(answer, false)
    }

    public void testCheckAnswerWithErrors() {
        def answer = """{
            "success": false,
            "error-codes": ["missing-input-response"]
            }"""
        buildAndCheckAnswer(answer, false)
    }

    private void buildAndCheckAnswer(String postText, boolean expectedValid) {
        def mocker = new MockFor(Post.class)
        mocker.demand.getQueryString(3..3) { new QueryString() }
        mocker.demand.getResponse { new JsonSlurper().parseText(postText) }
        mocker.use {
            def response = r.checkAnswer("123.123.123.123", "response")

            assertTrue response == expectedValid
        }
    }
}