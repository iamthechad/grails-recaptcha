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
    public static final String JS_URL = BASE_URL + ".js"
    private static final Map<String, String> PARAMETER_MAPPING = ['theme': 'theme', 'type': 'type', 'successCallback': 'callback', 'expiredCallback': 'expired-callback', 'tabindex': 'tabindex']
    private static final String AUTOMATIC_PREFIX = "data-"

    String publicKey
    String privateKey
    Boolean includeNoScript = false
    Boolean includeScript = true

    AuthenticatorProxy proxy = null

    /**
     * Creates HTML output with embedded recaptcha. The string response should be output on a HTML page (eg. inside a GSP).
     *
     * @param options Options for rendering, <code>theme</code>, <code>lang</code>, <code>type</code>, and <code>tabindex</code> are currently supported by recaptcha.
     *  The <code>includeScript</code> can also be specified and will override the global configuration setting.
     * @return
     */
    public String createRecaptchaHtml(Map options) {
        def includeScriptForInstance = includeScript
        if (options?.containsKey('includeScript')) {
            includeScriptForInstance = Boolean.valueOf(options.includeScript)
        }

        def message = new StringBuffer()

        if (includeScriptForInstance) {
            message << createScriptTag(options)
        }
        message << "<div class=\"g-recaptcha\" data-sitekey=\"${publicKey}\""
        PARAMETER_MAPPING.each { key, value ->
            if (null != options && options[key]) {
                message << " ${AUTOMATIC_PREFIX}${value}=\"${options[key]}\""
            }
        }
        message << "></div>\r\n"

        if (includeNoScript) {
            message << buildNoScript(publicKey)
        }

        return message.toString()
    }

    /**
     * Creates HTML output for creating an explicit recaptcha. The string response should be output on a HTML page (eg. inside a GSP).
     *
     * @param options Options for rendering. Only <code>lang</code> and <code>loadCallback</code> are supported here.
     * @return
     */
    public String createRecaptchaExplicitHtml(Map options) {
        def message = new StringBuffer()

        message << createScriptTagExplicit(options)

        if (includeNoScript) {
            message << buildNoScript(publicKey)
        }

        return message.toString()
    }

    /**
     * Create a JSON-like string that can be used to populate the recaptcha options when using explicit mode.
     * @param options Options for rendering. Supports <code>theme</code>, <code>type</code>, and <code>tabindex</code>.
     * @return
     */
    public String createRenderParameters(Map options) {
        def params = new StringBuffer()

        params << "{ 'sitekey': '${publicKey}'"
        PARAMETER_MAPPING.each { key, value ->
            if (null != options && options[key]) {
                params << ", '${value}': '${options[key]}'"
            }
        }
        params << "}"

        return params.toString()
    }

    /**
     * Create HTML output containing only the <code>script</code> tag required for ReCaptcha
     * @param options Options for creating the tag. Only <code>lang</code> is supported.
     * @return
     */
    public static String createScriptTag(Map options) {
        def qs = new QueryString()
        if (options?.lang) {
            qs.add("hl", URLEncoder.encode(options.remove("lang")))
        }
        return "<script src=\"${JS_URL}?${qs.toString()}\" async defer></script>"
    }

    /**
     * Create the script tag used for defining an explicit recaptcha instance.
     * @param options Options for creating the tag. Supports <code>lang</code> and <code>loadCallback</code>
     * @return
     */
    private static String createScriptTagExplicit(Map options) {
        def qs = new QueryString()
        if (options?.lang) {
            qs.add("hl", URLEncoder.encode(options.remove("lang")))
        }
        qs.add("render", "explicit")
        if (!options?.loadCallback) {
            throw new IllegalArgumentException("loadCallback parameter must be specified")
        }
        qs.add("onload", options?.loadCallback)
        return "<script src=\"${JS_URL}?${qs.toString()}\" async defer></script>"
    }

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
        return responseObject.success
    }
}