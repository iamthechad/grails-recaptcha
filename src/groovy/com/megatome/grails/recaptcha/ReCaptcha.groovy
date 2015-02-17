package com.megatome.grails.recaptcha

import com.megatome.grails.recaptcha.net.AuthenticatorProxy
import com.megatome.grails.recaptcha.net.Post
import com.megatome.grails.recaptcha.net.QueryString

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
 *
 * Based on recaptcha4j
 */

public class ReCaptcha {
    private static final String BASE_URL = "https://www.google.com/recaptcha/api"
    public static final String VERIFY_URL = "/siteverify"
    //public static final String AJAX_JS = "/js/recaptcha_ajax.js"
    public static final String JS_URL = BASE_URL + ".js"

    String publicKey
    String privateKey
    Boolean includeNoScript = false
    Boolean includeScript = true

    AuthenticatorProxy proxy = null

    /**
     * Creates HTML output with embedded recaptcha. The string response should be output on a HTML page (eg. inside a JSP).
     *
     * @param errorMessage An errormessage to display in the captcha, null if none.
     * @param options Options for rendering, <code>theme</code>, <code>lang</code>, and <code>type</code> are currently supported by recaptcha. You can
     *   put any options here though, and they will be added to the RecaptchaOptions javascript array.
     * @return
     */
    public String createRecaptchaHtml(Map options) {
        def qs = new QueryString()
        if (options?.lang) {
            qs.add("hl", URLEncoder.encode(options.remove("lang")))
        }

        def message = new StringBuffer()

        if (includeScript) {
            message << "<script src=\"${JS_URL}?${qs.toString()}\" async defer></script>"
        }
        message << "<div class=\"g-recaptcha\" data-sitekey=\"${publicKey}\""
        if (options?.theme) {
            message << " data-theme=\"${options.theme}\""
        }
        if (options?.type) {
            message << " data-type=\"${options.type}\""
        }
        message << "></div>\r\n"

        if (includeNoScript) {
            message << buildNoScript(publicKey)
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
    /*public String createRecaptchaAjaxHtml(Map options) {
        def qs = new QueryString()
        if (options?.lang) {
            qs.add("hl", URLEncoder.encode(options.remove("lang")))
        }

        def message = new StringBuffer()

        if (includeScript) {
            message <<  "<script type=\"text/javascript\" src=\"${recaptchaServer + AJAX_JS}\"></script>\r\n"
        }

        message << "<script type=\"text/javascript\">\r\nfunction showRecaptcha(element){Recaptcha.create(\"${publicKey}\", element, {" +
                options.collect { "$it.key:'${it.value}'" }.join(', ') + "});}\r\n</script>\r\n"

        if (includeNoScript) {
            message << buildNoScript(recaptchaServer, qs.toString())
        }

        return message.toString()
    }*/

    private static String buildNoScript(key) {
        return """<noscript>
        <div style=\"width: 302px; height: 352px;\">
        <div style=\"width: 302px; height: 352px; position: relative;\">
        <div style=\"width: 302px; height: 352px; position: absolute;\">
        <iframe src=\"$BASE_URL/fallback?k=$key\"
        frameborder=\"0\" scrolling=\"no\"
        style=\"width: 302px; height:352px; border-style: none;\">
        </iframe>
        </div>
        <div style=\"width: 250px; height: 80px; position: absolute; border-style: none;
        bottom: 21px; left: 25px; margin: 0px; padding: 0px; right: 25px;\">
        <textarea id=\"g-recaptcha-response\" name=\"g-recaptcha-response\"
        class=\"g-recaptcha-response\"
        style=\"width: 250px; height: 80px; border: 1px solid #c1c1c1;
        margin: 0px; padding: 0px; resize: none;\" value=\"\">
        </textarea>
        </div>
        </div>
        </div>
        </noscript>"""
    }

    /**
     * Validates a reCaptcha challenge and response.
     *
     * @param remoteAddr The address of the user, eg. request.getRemoteAddr()
     * @param response The response from the reCaptcha form, this is usually request.getParameter("g-recaptcha-response") in your code.
     * @return
     */
    public boolean checkAnswer(String remoteAddr, String response) {
        def post = new Post(url: BASE_URL + VERIFY_URL, proxy: proxy)
        post.queryString.add("secret", privateKey)
        post.queryString.add("response", response)
        post.queryString.add("remoteip", remoteAddr)

        def responseObject = post.response

        if (!responseObject) {
            return false
        }
        responseObject.success?.trim()?.toBoolean() == true
    }
}