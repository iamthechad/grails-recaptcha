
class RecaptchaGrailsPlugin {
    def version = "1.2.0"
    def grailsVersion = "2.0 > *"
    def dependsOn = [:]
    def author = "Chad Johnston"
    def authorEmail = "cjohnston@megatome.com"
    def title = "ReCaptcha and Mailhide support for Grails."
    def license = "APACHE"
    def issueManagement = [ system: "Github", url: "https://github.com/iamthechad/grails-recaptcha/issues" ]
    def scm = [ url: "https://github.com/iamthechad/grails-recaptcha" ]
    def description = '''\
Protect your website from spam and abuse while letting real people pass through with ease.
Version 1.0 of this plugin introduces support for the new "checkbox" ReCaptcha. Please use an older version if you
require the legacy functionality.

This plugin uses the ReCaptcha Java library available from http://code.google.com/p/recaptcha/.
'''
    def documentation = "http://iamthechad.github.com/grails-recaptcha/"
}
