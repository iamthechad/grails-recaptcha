package com.megatome.grails

import com.megatome.grails.recaptcha.ReCaptcha
import com.megatome.grails.recaptcha.net.AuthenticatorProxy
import com.megatome.grails.util.ConfigHelper
import grails.util.Environment

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

class RecaptchaService {
    boolean transactional = false
    def grailsApplication
    private def recaptchaConfig = null
    private def recap = null
    private def proxy = null

    /**
     * Gets the ReCaptcha config.
     */
    private def getRecaptchaConfig() {
        if (this.recaptchaConfig == null) {
            if (grailsApplication.config.recaptcha) {
                this.recaptchaConfig = grailsApplication.config.recaptcha
            } else {
                ClassLoader parent = getClass().getClassLoader()
                GroovyClassLoader loader = new GroovyClassLoader(parent)
                def rc = loader.loadClass("RecaptchaConfig")
                def cfg = new ConfigSlurper(Environment.current.name).parse(rc)
                this.recaptchaConfig = cfg.recaptcha
            }
            if (!this.recaptchaConfig.publicKey || this.recaptchaConfig.publicKey.length() == 0) {
                throw new IllegalArgumentException("ReCaptcha Public Key must be specified in RecaptchaConfig")
            }
            if (!this.recaptchaConfig.privateKey || this.recaptchaConfig.privateKey.length() == 0) {
                throw new IllegalArgumentException("ReCaptcha Private Key must be specified in RecaptchaConfig")
            }
        }
        return this.recaptchaConfig
    }

    private def getRecaptchaInstance() {
        if (!recap) {
            // Public key, private key, include noscript
            def config = getRecaptchaConfig()
            recap = new ReCaptcha(
                    publicKey: config.publicKey,
                    privateKey: config.privateKey,
                    includeNoScript: safeGetConfigValue('includeNoScript', true),
                    useSecureAPI: safeGetConfigValue('useSecureAPI', true),
                    forceLanguageInURL: safeGetConfigValue('forceLanguageInURL', false))
        }
        recap
    }

    private def getRecaptchaProxyInstance() {
        if (!proxy) {
            def config = getRecaptchaConfig().proxy
            println config
            proxy = new AuthenticatorProxy([
                    server: config.server,
                    port: config.containsKey('port') ? Integer.parseInt(config.port) : 80,
                    username: config.username,
                    password: config.password
            ])
        }
        proxy
    }

    private boolean safeGetConfigValue(def value, def defaultValue) {
        def config = getRecaptchaConfig()
        if (config.containsKey(value)) {
            return ConfigHelper.booleanValue(config[value])
        }
        log.error("Tried to access missing ReCaptcha value '" + value + "'. Using default value of '" + defaultValue + "'")
        defaultValue
    }

    /**
     * Creates HTML containing all necessary markup for displaying a ReCaptcha object. This method is most
     * commonly called by the ReCaptcha tag library and not by other users.
     *
     * @param session The current session. Used for short term storage of the recaptcha object and any error messages.
     * @param props Properties used to construct the HTML. See http://recaptcha.net/apidocs/captcha/client.html for valid
     * properties.
     *
     * @return HTML code, suitable for embedding into a webpage.
     */
    def createCaptcha(session, props) {
        return getRecaptchaInstance().createRecaptchaHtml(session["recaptcha_error"], props)
    }

    /**
     * Creates HTML containing all necessary markup for displaying an AJAX ReCaptcha object. This method is most
     * commonly called by the ReCaptcha tag library and not by other users.
     *
     * @param session The current session. Used for short term storage of the recaptcha object and any error messages.
     * @param props Properties used to construct the HTML. See http://recaptcha.net/apidocs/captcha/client.html for valid
     * properties.
     *
     * @return HTML code, suitable for embedding into a webpage.
     */
    def createCaptchaAjax(session, props) {
        return getRecaptchaInstance().createRecaptchaAjaxHtml(session['recaptcha_error'], props)
    }

    /**
     * Verify a ReCaptcha answer.
     *
     * @param session The current session.
     * @param remoteAddress The address of the browser submitting the answer.
     * @param params Parameters supplied by the browser.
     *
     * @return True if the supplied answer is correct, false otherwise. Returns true if ReCaptcha support is disabled.
     */
    def verifyAnswer(session, remoteAddress, params) {
        if (!isEnabled()) {
            return true
        }

        if (!recap) {
            return false
        } else {
            def response = recap.checkAnswer(getRecaptchaProxyInstance(), remoteAddress, params.recaptcha_challenge_field?.trim(), params.recaptcha_response_field?.trim())
            if (!response.valid) {
                session["recaptcha_error"] = response.errorMessage
            }
            return response.valid
        }
    }

    /**
     * Get a value indicating if the ReCaptcha plugin should be enabled.
     */
    def isEnabled() {
        return safeGetConfigValue('enabled', true)
    }

    /**
     * Get a value indicating if the previous verification attempt failed.
     *
     * @param session The current session
     */
    def validationFailed(session) {
        return (session["recaptcha_error"] != null)
    }

    /**
     * Cleanup resources associated with the session. This does have to be called, but not calling it will leave the recaptcha
     * object in memory until the session expires.
     *
     * @param session The current session.
     */
    def cleanUp(session) {
        session["recaptcha_error"] = null
    }
}
