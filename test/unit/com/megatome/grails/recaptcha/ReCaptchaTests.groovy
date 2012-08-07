package com.megatome.grails.recaptcha

import org.codehaus.groovy.grails.plugins.codecs.HTMLCodec
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

  private static final def HL = "&hl="
  private static final def HL_ENC = "&amp;hl="
  private static final def LANG = "lang:"

  private ReCaptcha r;

  protected void setUp() throws Exception {
    r = new ReCaptcha(privateKey: "testing", publicKey: "testing", includeNoScript: false, useSecureAPI: true)
	final codec = new HTMLCodec()
	String.metaClass."encodeAsHTML" = { -> codec.encode(delegate) }
  }

  public void testCreateCaptchaHtml() {

    assertTrue r.createRecaptchaHtml(null, null).contains("<script")

    r.includeNoScript = true

    assertTrue r.createRecaptchaHtml(null, null).contains("<noscript>")

    def html = r.createRecaptchaHtml(null, null)
    // no language specification
    assertFalse html.contains(HL_ENC)
    assertFalse html.contains(HL) // make sure that & are correctly encoded
	assertFalse html.contains(LANG)
	
    html = r.createRecaptchaHtml("The Error", null)
    assertTrue html.contains("&amp;error=The+Error")
    assertFalse html.contains("&error=The+Error") // make sure that & are correctly encoded
	assertFalse html.contains(LANG)
	
    html = r.createRecaptchaHtml(null, [lang:'de'])
	// useLangInUrl is by default false so no language specification in url
    assertFalse html.contains(HL_ENC)
    assertFalse html.contains(HL) // make sure that & are correctly encoded
    assertTrue html.contains("${LANG}'de'")

    def options = new Properties()
    options.setProperty("theme", "mytheme")
    options.setProperty("tabindex", "1")
    html = r.createRecaptchaHtml("The Error", options)
    assertTrue html.contains("theme:'mytheme'")
    assertTrue html.contains("tabindex:'1'")
    assertTrue html.contains(",")

  }
  
  public void testCreateCaptchaHtmlWithLang() {
    r.useLangInUrl = true
    def html = r.createRecaptchaHtml(null, null)
    assertFalse html.contains(HL_ENC)
    assertFalse html.contains(HL) // make sure that & are correctly encoded
    assertFalse html.contains(LANG)
    html = r.createRecaptchaHtml(null, [:])
    assertFalse html.contains(HL_ENC)
    assertFalse html.contains(HL) // make sure that & are correctly encoded
    assertFalse html.contains(LANG)
    html = r.createRecaptchaHtml(null, [lang:null])
    assertFalse html.contains(HL_ENC)
    assertFalse html.contains(HL) // make sure that & are correctly encoded
    html = r.createRecaptchaHtml(null, [lang:'de'])
    assertTrue html.contains("${HL_ENC}de")
    assertFalse html.contains("${HL}de") // make sure that & are correctly encoded
    assertTrue html.contains("${LANG}'de'")
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