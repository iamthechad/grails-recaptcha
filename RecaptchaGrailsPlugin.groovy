
class RecaptchaGrailsPlugin {
    def version = "0.5.1"
    def grailsVersion = "1.1 > *"
    def author = "Chad Johnston"
    def authorEmail = "cjohnston@megatome.com"
    def title = "This plugin adds ReCaptcha and Mailhide support to Grails."
    def description = '''\
ReCaptcha is a CAPTCHA implementation that has a goal of better digitizing books by having users identify words that traditional OCR systems have missed.
This plugin uses the ReCaptcha Java library available from http://code.google.com/p/recaptcha/.

Licensed under the Apache License, Version 2.0
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

NOTE: Versions 0.4.x and 0.5.x of this plugin are for Grails 1.1 and later. If you are using Grails 1.0.x, please use the latest 0.3.x version of this plugin.
'''
    def documentation = "http://grails.codehaus.org/ReCaptcha+Plugin"
}
