package com.megatome.grails.recaptcha

import com.megatome.grails.recaptcha.net.Post
import com.megatome.grails.recaptcha.net.QueryString

/**
 * Copyright 2010-2013 Megatome Technologies
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
 *
 * Based on recaptcha4j
 */

public class ReCaptcha {
    public static final String HTTP_SERVER = "http://www.google.com/recaptcha/api"
    public static final String HTTPS_SERVER = "https://www.google.com/recaptcha/api"
    public static final String VERIFY_URL = "/verify"
    public static final String AJAX_JS = "/js/recaptcha_ajax.js"

    String publicKey
    String privateKey
    Boolean includeNoScript = false
    Boolean useSecureAPI
    // Put the hl parameter into the challenge URL to force language change
    Boolean forceLanguageInURL = false

    /**
     * Creates HTML output with embedded recaptcha. The string response should be output on a HTML page (eg. inside a JSP).
     *
     * @param errorMessage An errormessage to display in the captcha, null if none.
     * @param options Options for rendering, <code>tabindex</code> and <code>theme</code> are currently supported by recaptcha. You can
     *   put any options here though, and they will be added to the RecaptchaOptions javascript array.
     * @return
     */
    public String createRecaptchaHtml(String errorMessage, Map options) {
        def recaptchaServer = useSecureAPI ? HTTPS_SERVER : HTTP_SERVER
        def qs = new QueryString([k: publicKey, error: errorMessage])
        if (forceLanguageInURL && options?.lang) {
            qs.add("hl", URLEncoder.encode(options.remove("lang")))
        }

        def message = new StringBuffer()
        if (options) {
            message << "<script type=\"text/javascript\">\r\nvar RecaptchaOptions = {" +
                    options.collect { "$it.key:'${it.value}'" }.join(', ') +
                    "};\r\n</script>\r\n"
        }

        message << "<script type=\"text/javascript\" src=\"$recaptchaServer/challenge?${qs.toString()}\"></script>\r\n"

        if (includeNoScript) {
            message << buildNoScript(recaptchaServer, qs.toString())
        }

        return message.toString()
    }

    /**
     * Creates HTML output with embedded recaptcha AJAX. The string response should be output on a HTML page (eg. inside a JSP).
     *
     * @param errorMessage An errormessage to display in the captcha, null if none.
     * @param options Options for rendering, <code>tabindex</code> and <code>theme</code> are currently supported by recaptcha. You can
     *   put any options here though, and they will be added to the RecaptchaOptions javascript array.
     * @return
     */
    public String createRecaptchaAjaxHtml(String errorMessage, Map options) {
        def recaptchaServer = useSecureAPI ? HTTPS_SERVER : HTTP_SERVER
        def qs = new QueryString([k: publicKey, error: errorMessage])
        if (forceLanguageInURL && options?.lang) {
            qs.add("hl", URLEncoder.encode(options.remove("lang")))
        }

        def message = new StringBuffer()

        message <<  "<script type=\"text/javascript\" src=\"${recaptchaServer + AJAX_JS}\"></script>\r\n"

        message << "<script type=\"text/javascript\">\r\nfunction showRecaptcha(element){Recaptcha.create(\"${publicKey}\", element, {" +
                options.collect { "$it.key:'${it.value}'" }.join(', ') + "});}\r\n</script>\r\n"

        if (includeNoScript) {
            message << buildNoScript(recaptchaServer, qs.toString())
        }

        return message.toString()
    }

    private static String buildNoScript(String server, String queryString) {
        return """<noscript>
      <iframe src=\"$server/noscript?$queryString\" height=\"300\" width=\"500\" frameborder=\"0\"></iframe><br>
      <textarea name=\"recaptcha_challenge_field\" rows=\"3\" cols=\"40\"></textarea>
      <input type=\"hidden\" name=\"recaptcha_response_field\" value=\"manual_challenge\">
      </noscript>"""
    }

    /**
     * Validates a reCaptcha challenge and response.
     *
     * @param remoteAddr The address of the user, eg. request.getRemoteAddr()
     * @param challenge The challenge from the reCaptcha form, this is usually request.getParameter("recaptcha_challenge_field") in your code.
     * @param response The response from the reCaptcha form, this is usually request.getParameter("recaptcha_response_field") in your code.
     * @return
     */
    public Map checkAnswer(String remoteAddr, String challenge, String response) {
        def recaptchaServer = useSecureAPI ? HTTPS_SERVER : HTTP_SERVER
        def post = new Post(url: recaptchaServer + VERIFY_URL)
        post.queryString.add("privatekey", privateKey)
        post.queryString.add("remoteip", remoteAddr)
        post.queryString.add("challenge", challenge)
        post.queryString.add("response", response)

        def responseMessage = post.text

        if (!responseMessage) {
            return [valid: false, errorMessage: "Null read from server."]
        }

        def a = responseMessage.split("\r?\n") as List
        if (a.isEmpty()) {
            return [valid: false, errorMessage: "No answer returned from recaptcha: $responseMessage"]
        }
        def isValid = "true".equals(a[0])
        def errorMessage = null;
        if (!isValid) {
            errorMessage = a[1] ?: "Unknown error"
        }

        [valid: isValid, errorMessage: errorMessage]
    }
}