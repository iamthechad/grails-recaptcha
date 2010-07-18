package com.megatome.grails.recaptcha

import com.megatome.grails.recaptcha.net.Post
import groovy.mock.interceptor.MockFor
import com.megatome.grails.recaptcha.net.QueryString

/**
 * Copyright 2010 Megatome Technologies
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
    r = new ReCaptcha(privateKey: "testing", publicKey: "testing", includeNoScript: false, useSecureAPI: true)
  }

  public void testCreateCaptchaHtml() {

    assertTrue r.createRecaptchaHtml(null, null).contains("<script")

    r.includeNoScript = true

    assertTrue r.createRecaptchaHtml(null, null).contains("<noscript>")


    assertTrue r.createRecaptchaHtml("The Error", null).contains("&error=The+Error")

    def options = new Properties()
    options.setProperty("theme", "mytheme")
    options.setProperty("tabindex", "1")
    def html = r.createRecaptchaHtml("The Error", options)
    assertTrue html.contains("theme:'mytheme'")
    assertTrue html.contains("tabindex:'1'")
    assertTrue html.contains(",")

  }

  public void testCheckAnswer() {
    buildAndCheckAnswer("true\nnone", true, null)
  }

  public void testCheckAnswer_02() {
    buildAndCheckAnswer("true\n", true, null)
  }

  public void testCheckAnswer_03() {
    buildAndCheckAnswer("true", true, null)
  }

  public void testCheckAnswer_04() {
    buildAndCheckAnswer("false", false, "Unknown error")
  }

  public void testCheckAnswer_05() {
    buildAndCheckAnswer("nottrue", false, "Unknown error")
  }

  public void testCheckAnswer_06() {
    buildAndCheckAnswer("false\nblabla", false, "blabla")
  }

  public void testCheckAnswer_07() {
    buildAndCheckAnswer("false\nblabla\n\n", false, "blabla") 
  }

  private void buildAndCheckAnswer(def postText, def expectedValid, def expectedErrorMessage) {
    def mocker = new MockFor(Post.class)
    mocker.demand.getQueryString(4..4) { new QueryString() }
    mocker.demand.getText { postText }
    mocker.use {

      def response = r.checkAnswer("123.123.123.123", "abcdefghijklmnop", "response")

      assertTrue response.valid == expectedValid
      assertEquals expectedErrorMessage, response.errorMessage
    }
  }
}