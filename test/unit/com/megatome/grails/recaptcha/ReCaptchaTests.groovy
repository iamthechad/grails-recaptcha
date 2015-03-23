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

    public void testCreateCaptchaHtmlScriptOptions() {
        r.includeScript = false
        assertFalse r.createRecaptchaHtml(null).contains("<script")

        r.includeScript = true
        assertTrue r.createRecaptchaHtml(null).contains("<script")

        r.includeNoScript = true
        assertTrue r.createRecaptchaHtml(null).contains("<noscript>")
    }

    public void testCreateCaptchaHtmlOptions() {
        def html = r.createRecaptchaHtml(null)
        assertTrue html.contains("g-recaptcha")
        assertTrue html.contains("data-sitekey")
        assertFalse html.contains("data-theme")
        assertFalse html.contains("data-tabindex")
        assertFalse html.contains("data-type")

        assertTrue r.createRecaptchaHtml(["theme": "mytheme"]).contains("data-theme=\"mytheme\"")

        assertTrue r.createRecaptchaHtml(["tabindex": "0"]).contains("data-tabindex=\"0\"")

        assertTrue r.createRecaptchaHtml(["type": "image"]).contains("data-type=\"image\"")
    }

    public void testCreateCaptchaHtmlWithLangInOptions() {
        def html = r.createRecaptchaHtml(["lang": "fr"])
        assertTrue html.contains("<script")
        assertTrue html.contains("hl=fr")

        html = r.createRecaptchaHtml([:])
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