
class RecaptchaGrailsPlugin {
    def version = "0.6.6"
    def grailsVersion = "2.0 > *"
    def dependsOn = [:]
    def author = "Chad Johnston"
    def authorEmail = "cjohnston@megatome.com"
    def title = "ReCaptcha and Mailhide support for Grails."
    def license = "APACHE"
    def issueManagement = [ system: "Github", url: "https://github.com/iamthechad/grails-recaptcha/issues" ]
    def scm = [ url: "https://github.com/iamthechad/grails-recaptcha" ]
    def description = '''\
ReCaptcha is a CAPTCHA implementation that has a goal of better digitizing books by having users identify words that traditional OCR systems have missed.
This plugin uses the ReCaptcha Java library available from http://code.google.com/p/recaptcha/.

Versions 0.5 and below of this plugin will not work. ReCaptcha has changed the URL used for secure connections.
'''
    def documentation = "http://iamthechad.github.com/grails-recaptcha/"
}
