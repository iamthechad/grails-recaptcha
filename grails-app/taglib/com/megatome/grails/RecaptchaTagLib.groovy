package com.megatome.grails

/**
 * Copyright 2010-2014 Megatome Technologies
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

class RecaptchaTagLib {
    static namespace = "recaptcha"
    RecaptchaService recaptchaService
    MailhideService mailhideService
    private def attrNames = ["theme", "lang", "type", "callback"]

    /**
     * Evaluates the content of the tag if ReCaptcha support is enabled. This value is set in config.
     */
    def ifEnabled = { attrs, body ->
        if (recaptchaService.isEnabled()) {
            out << body()
        }
    }

    /**
     * Evaluates the content of the tag if ReCaptcha support is disabled. This value is set in config.
     */
    def ifDisabled = { attrs, body ->
        if (!recaptchaService.isEnabled()) {
            out << body()
        }
    }

    /**
     * Create and display a ReCaptcha instance. Supports the following attributes:
     * <ul>
     * <li>theme - Can be one of 'dark' or 'light'. Defaults to 'light'</li>
     * <li>lang  - Can be one of 'en','nl','fr','de','pt','ru','es','tr'</li>
     * <li>type - Can be one of 'image' or 'audio'. Defaults to 'image'</li>
     * </ul>
     *
     * This tag can also be used in support of a custom theme. For more information about
     * custom themes, see: http://recaptcha.net/apidocs/captcha/client.html
     */
    def recaptcha = { attrs ->
        def props = new Properties()
        attrNames.each {
            if (attrs[it]) {
                props.setProperty(it, attrs[it])
            }
        }
        out << recaptchaService.createCaptcha(props)
    }

    /**
     * Create and AJAX ReCaptcha instance. The instance is not visible by default, and can be shown
     * by calling the showRecaptcha method with the name of the div that the widget should be displayed in.
     * Supports the following attributes:
     * <ul>
     * <li>theme - Can be one of 'dark' or 'light'. Defaults to 'light'</li>
     * <li>lang  - Can be one of 'en','nl','fr','de','pt','ru','es','tr'</li>
     * <li>type - Can be one of 'image' or 'audio'. Defaults to 'image'</li>
     * <li>callback - Callback.</li>
     * </ul>
     *
     * This tag can also be used in support of a custom theme. For more information about
     * custom themes, see: http://recaptcha.net/apidocs/captcha/client.html
     */
    /*def recaptchaAjax = { attrs ->
        def props = new Properties()
        attrNames.each {
            if (attrs[it]) {
                props.setProperty(it, attrs[it])
            }
        }

        out << recaptchaService.createCaptchaAjax(props)
    }*/

    /**
     * Evaluates the content of the tag if ReCaptcha validation failed. This will allow
     * developers to display errors for ReCaptcha themes that do not display error messages
     * by default, like the 'clean' theme.
     */
    def ifFailed = { attrs, body ->
        if (recaptchaService.validationFailed(session)) {
            out << body()
        }
    }

    /**
     * Creates a link that conforms to the recommended Mailhide usage. The created link will pop up a new window.
     */
    def mailhide = { attrs, body ->
        if (!attrs.emailAddress) {
            throw new IllegalArgumentException("Email address must be specified in mailhide tag")
        }

        def url = mailhideService.createMailhideURL(attrs.emailAddress)
        def popupCmd = "onclick=\"window.open('${url}', '', 'toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=0,width=500,height=300'); return false;\""
        def link = "<a href=\"${url}\" ${popupCmd} title=\"Reveal this e-mail address\">${body()}</a>"
        out << link
    }

    /**
     * Creates a raw Mailhide URL in case the default behavior is not desired. The created URL will be placed into the
     * variable named by the "var" attribute. If this attribute is left out, the URL will be placed into a variable
     * named "mailhideURL". 
     */
    def mailhideURL = {attrs, body ->
        if (!attrs.emailAddress) {
            throw new IllegalArgumentException("Email address must be specified in mailhideURL tag")
        }

        def url = mailhideService.createMailhideURL(attrs.emailAddress)
        def var = attrs.var ? attrs.var : "mailhideURL"
        out << body((var): url)
    }
}