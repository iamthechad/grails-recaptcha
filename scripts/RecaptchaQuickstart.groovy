includeTargets << grailsScript("_GrailsBootstrap")

USAGE = """
Usage: grails recaptcha-quickstart <standalone|integrated>

Creates the required configuration elements for using the ReCaptcha plugin.
If 'integrated' is specified, entries are created in Config.groovy.
If 'standalone' is specified, then RecaptchaConfig.groovy is created.
"""

target(main: "Create default ReCaptcha configuration") {
    def argValue = parseArgs()
    if (!argValue) {
        return 1
    }
    if ("standalone" == argValue) {
        updateConfig()
    } else if ("integrated" == argValue) {
        copyConfig()
    }

    printMessage """
*************************************************
ReCaptcha configuration created successfully.
Please be sure to enter your API keys before use.
*************************************************
"""
}

private parseArgs() {
    def args = argsMap.params

    if (1 == args.size()) {
        if ("standalone" == $args[0]) {
            printMessage "Creating standalone ReCaptcha configuration in RecaptchaConfig.groovy"
        } else if ("integrated" == args[0]) {
            printMessage "Creating ReCapctcha configuration in Config.groovy"
        } else {
            errorMessage USAGE
            return null
        }
        return args[0]
    }

    errorMessage USAGE
    null
}

printMessage = { String message -> event('StatusUpdate', [message]) }
errorMessage = { String message -> event('StatusError', [message]) }

setDefaultTarget(main)
