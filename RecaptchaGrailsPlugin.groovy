
class RecaptchaGrailsPlugin {
    def version = "0.5.2"
    def grailsVersion = "1.1 > *"
    def author = "Chad Johnston"
    def authorEmail = "cjohnston@megatome.com"
    def title = "This plugin adds ReCaptcha and Mailhide support to Grails."
    def description = '''\
ReCaptcha is a CAPTCHA implementation that has a goal of better digitizing books by having users identify words that traditional OCR systems have missed.
This plugin uses the ReCaptcha Java library available from http://code.google.com/p/recaptcha/.

Licensed under the Apache License, Version 2.0
You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0

Versions 0.5 and below of this plugin may not work as desired. ReCaptcha has changed the URL used for secure connections, and these older versions will likely stop functioning at some point in the near future.
'''
    def documentation = "http://grails.codehaus.org/ReCaptcha+Plugin"
}
