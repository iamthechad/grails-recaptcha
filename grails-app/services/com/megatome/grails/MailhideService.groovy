package com.megatome.grails

import org.codehaus.groovy.grails.commons.ConfigurationHolder
import grails.util.GrailsUtil
import com.megatome.grails.mailhide.security.MailhideEncryption
import com.megatome.grails.mailhide.util.StringUtils

class MailhideService {
    boolean transactional = false
	private def mailhideConfig = null
    private def cachedEmail = [:]

    /**
	 * Gets the ReCaptcha config as defined in grails-app/conf/RecaptchaConfig.grovy
	 */
	private def getMailhideConfig() {
		if(this.mailhideConfig==null){
            if (ConfigurationHolder.config.mailhide) {
              this.mailhideConfig = ConfigurationHolder.config.mailhide
            } else {
              ClassLoader parent = getClass().getClassLoader()
              GroovyClassLoader loader = new GroovyClassLoader(parent)
              def rc = loader.loadClass("RecaptchaConfig")
              def cfg = new ConfigSlurper(GrailsUtil.environment).parse(rc)
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
