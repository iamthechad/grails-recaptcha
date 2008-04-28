
class RecaptchaTagLib {
	static namespace = "recaptcha"
	RecaptchaService recaptchaService
	private def attrNames = ["theme", "lang", "tabindex", "custom_theme_widget"]
	
	/**
	 * Evaluates the content of the tag if ReCaptcha support is enabled. This value is set in 
	 * grails-app/conf/RecaptchaConfig.groovy
	 */
	def ifEnabled = { attrs, body ->
		if (recaptchaService.getRecaptchaConfig()?.recaptcha.enabled) {
			out << body()
		}
	}
	
	/**
	 * Create and display a ReCaptcha instance. Supports the following attributes:
	 * <ul>
	 * <li>theme - Can be one of 'red','white','blackglass','clean','custom'</li>
	 * <li>lang  - Can be one of 'en','nl','fr','de','pt','ru','es','tr'</li>
	 * <li>tabindex - Sets a tabindex for the ReCaptcha box</li>
	 * <li>custom_theme_widget - Used when specifying a custom theme.</li>
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
		out << recaptchaService.createCaptcha(session, props)
	}
}