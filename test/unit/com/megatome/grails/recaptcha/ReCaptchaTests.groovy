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


    public void testCreateCaptchaHtmlWithLangInURL() {
        def recap = new ReCaptcha(privateKey: "testing", publicKey: "testing", includeNoScript: false, includeScript: true)

        def options = [:]
        options.lang = "fr"
        def html = recap.createRecaptchaHtml(options)
        assertTrue html.contains("<script")
        assertTrue html.contains("hl=fr")

        options.lang = null
        html = recap.createRecaptchaHtml(options)
        assertTrue html.contains("<script")
        assertFalse html.contains("hl=fr")
    }

    public void testCheckAnswer() {
        buildAndCheckAnswer("true\nnone", false)
    }

    public void testCheckAnswer_02() {
        buildAndCheckAnswer("true\n", true)
    }

    public void testCheckAnswer_03() {
        buildAndCheckAnswer("true", true)
    }

    public void testCheckAnswer_04() {
        buildAndCheckAnswer("false", false)
    }

    public void testCheckAnswer_05() {
        buildAndCheckAnswer("nottrue", false)
    }

    public void testCheckAnswer_06() {
        buildAndCheckAnswer("false\nblabla", false)
    }

    public void testCheckAnswer_07() {
        buildAndCheckAnswer("false\nblabla\n\n", false)
    }

    private void buildAndCheckAnswer(String postText, boolean expectedValid) {
        def mocker = new MockFor(Post.class)
        mocker.demand.getQueryString(3..3) { new QueryString() }
        mocker.demand.getResponse { new JsonSlurper().parseText("{\"success\":\"${postText}\"}") }
        mocker.use {
            def response = r.checkAnswer("123.123.123.123", "response")

            assertTrue response == expectedValid
        }
    }
}