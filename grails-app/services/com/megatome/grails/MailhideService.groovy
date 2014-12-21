package com.megatome.grails

import grails.util.Environment
import grails.util.GrailsUtil
import com.megatome.grails.mailhide.security.MailhideEncryption
import com.megatome.grails.mailhide.util.StringUtils

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

class MailhideService {
    boolean transactional = false
    def grailsApplication
    private def mailhideConfig = null
    private def cachedEmail = [:]

    /**
     * Gets the Mailhide config.
     */
    private def getMailhideConfig() {
        if (this.mailhideConfig == null) {
            if (grailsApplication.config.mailhide) {
                this.mailhideConfig = grailsApplication.config.mailhide
            } else {
                ClassLoader parent = getClass().getClassLoader()
                GroovyClassLoader loader = new GroovyClassLoader(parent)
                def rc = loader.loadClass("RecaptchaConfig")
                def cfg = new ConfigSlurper(Environment.current.name).parse(rc)
                this.mailhideConfig = cfg.mailhide
            }
            if (!this.mailhideConfig.publicKey || this.mailhideConfig.publicKey.length() == 0) {
                throw new IllegalArgumentException("Mailhide Public Key must be specified in RecaptchaConfig")
            }
            if (!this.mailhideConfig.privateKey || this.mailhideConfig.privateKey.length() == 0) {
                throw new IllegalArgumentException("Mailhide Private Key must be specified in RecaptchaConfig")
            }
        }
        return this.mailhideConfig
    }

    /**
     * Create a Mailhide URL from the specified email address.
     * @param emailAddress
     * @return
     */
    def createMailhideURL(emailAddress) {
        def config = getMailhideConfig()
        def paddedEmail = StringUtils.padString(emailAddress)
        def encryptedEmail = cachedEmail[emailAddress] ?: MailhideEncryption.encrypt(paddedEmail, config.privateKey).encodeAsURLSafeBase64()
        if (!cachedEmail[emailAddress]) {
            cachedEmail[emailAddress] = encryptedEmail
        }
        return "http://www.google.com/recaptcha/mailhide/d?k=${config.publicKey}&c=${encryptedEmail}"
    }
}
