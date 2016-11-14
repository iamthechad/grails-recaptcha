[![Build Status](https://travis-ci.org/iamthechad/grails-recaptcha.svg?branch=master)](https://travis-ci.org/iamthechad/grails-recaptcha)
[![Coverage Status](https://coveralls.io/repos/iamthechad/grails-recaptcha/badge.svg?branch=master&service=github)](https://coveralls.io/github/iamthechad/grails-recaptcha?branch=master)
[![Stories in Ready](https://badge.waffle.io/iamthechad/grails-recaptcha.png?label=ready&title=Ready)](https://waffle.io/iamthechad/grails-recaptcha)
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Badges](http://img.shields.io/:badges-5/5-ff6799.svg)](https://github.com/badges/badgerbadgerbadger)

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [Version 1.0 Notice](#version-10-notice)
- [Introduction](#introduction)
- [Installation](#installation)
  - [Integrated Configuration](#integrated-configuration)
  - [Standalone Configuration](#standalone-configuration)
  - [Externalized Configuration](#externalized-configuration)
    - [Version 1.0 differences](#version-10-differences)
- [Usage - ReCaptcha](#usage---recaptcha)
  - [Edit the Configuration](#edit-the-configuration)
    - [Proxy Server Configuration](#proxy-server-configuration)
  - [Use the Tag Library](#use-the-tag-library)
    - [`<recaptcha:ifEnabled>`](#recaptchaifenabled)
    - [`<recaptcha:ifDisabled>`](#recaptchaifdisabled)
    - [`<recaptcha:recaptcha>`](#recaptcharecaptcha)
    - [`<recaptcha:script>`](#recaptchascript)
    - [`<recaptcha:recaptchaExplicit>`](#recaptcharecaptchaexplicit)
    - [`<recaptcha:renderParameters>`](#recaptcharenderparameters)
    - [`<recaptcha:ifFailed>`](#recaptchaiffailed)
  - [Verify the Captcha](#verify-the-captcha)
  - [Examples](#examples)
    - [Tag Usage for Automatic Rendering](#tag-usage-for-automatic-rendering)
    - [Tag Usage for Explicit Rendering](#tag-usage-for-explicit-rendering)
    - [Tag Usage with Separate Script](#tag-usage-with-separate-script)
    - [Customizing the Language](#customizing-the-language)
    - [Verify User Input](#verify-user-input)
    - [Testing](#testing)
- [Contributing](#contributing)
  - [Before Submission](#before-submission)
  - [When Submitting](#when-submitting)
- [Misc.](#misc)
  - [CHANGELOG (PRE 0.6.0)](#changelog-pre-060)
  - [Thanks](#thanks)
- [Suggestions or Comments](#suggestions-or-comments)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

![Target Branch](https://img.shields.io/badge/target%20branch-version1__7__0-green.svg?style=flat-square)

# Version 1.0 Notice

Beginning with version 1.0 of this plugin, only the new "checkbox" captcha is supported. Please use version 0.7.0 if you require the legacy functionality.

# Introduction

This plugin is designed to make using the ReCaptcha service within Grails easy. In order to use this plugin, you must have a ReCaptcha account, available from [http://www.google.com/recaptcha](http://www.google.com/recaptcha).

# Installation

Add the following to your `grails-app/conf/BuildConfig.groovy`

    …
    plugins {
        …
        compile ':recaptcha:1.7.0'
        …
    }
    
Upon installation, run `grails recaptcha-quickstart` to create the skeleton configuration. The quickstart has two targets: `integrated` or `standalone`, depending on where you'd like the configuration to live.

## Integrated Configuration
Integrated configuration adds the following to the end of your `Config.groovy` file:

    // Added by the Recaptcha plugin:
    recaptcha {
        // These keys are generated by the ReCaptcha service
        publicKey = ""
        privateKey = ""

        // Include the noscript tags in the generated captcha
        includeNoScript = true

        // Include the required script tag with the generated captcha
        includeScript = true

        // Set to false to disable the display of captcha
        enabled = true
    }
    
This configuration can be modified to mimic the standalone if there is a need for different behavior depending on the current environment.

## Standalone Configuration
Standalone configuration creates a file called  `RecaptchaConfig.groovy`  in  `grails-app/conf` with the following content:

	recaptcha {
	    // These keys are generated by the ReCaptcha service
	    publicKey = ""
	    privateKey = ""

	    // Include the noscript tags in the generated captcha
	    includeNoScript = true

	    // Include the required script tag with the generated captcha
        includeScript = true
	}

	environments {
	  development {
	    recaptcha {
	      // Set to false to disable the display of captcha
	      enabled = false
	    }
	  }
	  production {
	    recaptcha {
	      // Set to false to disable the display of captcha
	      enabled = true
	    }
	  }
	}

## Externalized Configuration
See the Grails docs for examples of using externalized configuration files. The ReCaptcha config can be externalized as
the `.groovy` file (easiest), or it can be converted into a Java `.properties` file.

### Version 1.0 differences

The following configuration properties are no longer used and will be ignored if they are present in the config:

* `useSecureAPI` - All communications to the ReCaptcha servers are performed over HTTPS. There is no option to use HTTP.
* `forceLanguageInURL` - ReCaptcha now properly displays the captcha in the selected language. We no longer have to force the language.

# Usage - ReCaptcha

The plugin is simple to use. In order to use it, there are four basic steps:

## Edit the Configuration

The configuration values are pretty self-explanatory, and match with values used by the ReCaptcha service. You must enter your public and private ReCaptcha keys, or errors will be thrown when trying to display a captcha.

### Proxy Server Configuration

If your server needs to connect through a proxy to the ReCaptcha service, add the following to the ReCapctcha configuration. **These properties are not created by the quickstart script. They must be added manually.**

    proxy {
        server = ""   // IP or hostname of proxy server
        port = ""     // Proxy server port, defaults to 80
        username = "" // Optional username if proxy requires authentication
        password = "" // Optional password if proxy requires authentication
    }

Only the `server` property is required. The `port` will default to `80` if not specified. The `username` and `password` properties need to be specified only when the proxy requires authentication.

Like other configurations, this can be placed at the top-level `recaptcha` entry, or it can be specified on a per-environment basis.

## Use the Tag Library

### `<recaptcha:ifEnabled>`

This tag is a simple utility that will render its contents if the captcha is enabled in the configuration.

### `<recaptcha:ifDisabled>`

This tag is a simple utility that will render its contents if the captcha is disabled in the configuration.

### `<recaptcha:recaptcha>`

This tag is responsible for generating the correct HTML output to display the captcha. It supports the following attributes: 

* `theme` - Can be one of `dark` or `light`. Defaults to `light`.
* `size` - Can be one of `compact` or `normal`. Defaults to `normal`.
* `lang` - Can be any one of the supported ReCaptcha language codes. See the [list of supported language codes](https://developers.google.com/recaptcha/docs/language).
* `tabindex` - Optional tabindex of the widget. 
* `type` - Type of captcha to display if the checkbox is not sufficient. Can be one of `image` or `audio`. Defaults to `image`.
* `successCallback` - Optional function to be called when the user submits a successful response.
* `expiredCallback` - Optional function to be called when the successful response has expired.
* `includeScript` - If `includeScript` is set to `false` at either the global or tag level, the `<script>` tag required by ReCaptcha will not be included in the generated HTML. The `<recaptcha:script>` tag is also required in this scenario.

See the [ReCaptcha Client Guide](https://developers.google.com/recaptcha/docs/display#config) for more details.

### `<recaptcha:script>`

This tag will render the required `<script>` tag. Combine this with the global or tag-level `includeScript=false` setting to allow putting the `<script>` tag elsewhere in your markup. This tag also supports the "lang" attribute. **This does not work in the `<head>` section of the page**

### `<recaptcha:recaptchaExplicit>`

This tag is responsible for generating the correct HTML output to support explicit display and usage of the captcha. It supports the following attributes: 

* `lang` - Can be any one of the supported ReCaptcha language codes. See the [list of supported language codes](https://developers.google.com/recaptcha/docs/language).
* `loadCallback` - The JavaScript function to be called when all dependencies have loaded. This function is usually responsible for rendering the captcha.

For more information about explicit mode captchas, see [the ReCaptcha documentation](https://developers.google.com/recaptcha/docs/display#explicit_render).

### `<recaptcha:renderParameters>`

This utility tag will generate the JSON string used as a parameter to the `grecaptcha.render()` function. It supports the following attributes:

* `theme` - Can be one of `dark` or `light`. Defaults to `light`.
* `size` - Can be one of `compact` or `normal`. Defaults to `normal`.
* `tabindex` - Optional tabindex of the widget. 
* `type` - Type of captcha to display if the checkbox is not sufficient. Can be one of `image` or `audio`. Defaults to `image`.
* `successCallback` - Optional function to be called when the user submits a successful response.
* `expiredCallback` - Optional function to be called when the successful response has expired.
 
See the [ReCaptcha Client Guide](https://developers.google.com/recaptcha/docs/display#config) for more details.

### `<recaptcha:ifFailed>`

This tag will render its contents if the previous validation failed.

## Verify the Captcha

In your controller, call `recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)` to verify the answer provided by the user. This method will return true or false. Also note that `verifyAnswer` will return `true` if the plugin has been disabled in the configuration - this means you won't have to change your controller.

## Examples

Here's a simple example pulled from an account creation application.

### Tag Usage for Automatic Rendering

This is the most common usage scenario.

In our GSP, we add the code to show the captcha:

    <recaptcha:ifEnabled>
        <recaptcha:recaptcha theme="dark"/>
    </recaptcha:ifEnabled>

In this example, we're using ReCaptcha's `dark` theme. Leaving out the `theme` attribute will default the captcha to the `light` theme.

### Tag Usage for Explicit Rendering
 
In our GSP, we add code like the following:

    <script type="text/javascript">
      var onloadCallback = function() {
        grecaptcha.render('html_element', <recaptcha:renderParameters theme="dark" type="audio" tabindex="2"/>);
      };
    </script>
    <g:form action="myAction" method="post">
      <recaptcha:ifEnabled>
        <recaptcha:recaptchaExplicit loadCallback="onloadCallback"/>
        <div id="html_element"></div>
      </recaptcha:ifEnabled>
      <br/>
      <g:submitButton name="submit"/>
    </g:form>
    
In this example, we're using ReCaptcha's `dark` theme, with an `audio` captcha and a `tabindex` of 2.

For more information about explicit mode captchas, see [the ReCaptcha documentation](https://developers.google.com/recaptcha/docs/display#explicit_render).

### Tag Usage with Separate Script

Set the `includeScript` value to `false` either at the tag level (below), or in the global ReCaptcha settings.

    <body>
      <g:form action="validateNormal" method="post" >
        <recaptcha:ifEnabled>
          <recaptcha:recaptcha includeScript="false"/>
        </recaptcha:ifEnabled>
        <br/>
        <g:submitButton name="submit"/>
      </g:form>
      <recaptcha:script/>
    </body>

This will cause the `<script src="https://www.google.com/recaptcha/api.js?" async="" defer=""></script>` tag to be output separately at the bottom of the document instead of just before the `<div>` containing the captcha.

### Customizing the Language

If you want to change the language your captcha uses, set `lang = "someLang"` in the `<recaptcha:recaptcha>` or `<recaptcha:recaptchaExplcit>` tags.

See [ReCaptcha Language Codes](https://developers.google.com/recaptcha/docs/language) for available languages.

### Verify User Input

Here's an abbreviated controller class that verifies the captcha value when a new user is saved:

	import com.megatome.grails.RecaptchaService
	class UserController {
		RecaptchaService recaptchaService

		def save = {
			def user = new User(params)
			...other validation...
			def recaptchaOK = true
			if (!recaptchaService.verifyAnswer(session, request.getRemoteAddr(), params)) {
				recaptchaOK = false
			}
			if(!user.hasErrors() && recaptchaOK && user.save()) {
				recaptchaService.cleanUp(session)
				...other account creation acivities...
				render(view:'showConfirmation',model:[user:user])
			}
			else {
				render(view:'create',model:[user:user])
			}
		}
	}


### Testing

Starting with version 0.4.5, the plugin should be easier to integrate into test scenarios. You can look at the test cases in the plugin itself, or you can implement something similar to:

	private void buildAndCheckAnswer(String postText, boolean expectedValid) {
        def stub = new StubFor(Post.class)
        stub.demand.hasProperty(3..3) { true }
        stub.demand.setUrl() {}
        stub.demand.setProxy() {}
        stub.demand.getQueryParams(3..3) { new QueryParams(null) }
        stub.demand.getResponse() { postText == null ? null : new JsonSlurper().parseText(postText) }
    
        stub.use {
            def response = r.checkAnswer("123.123.123.123", "response")

            assertTrue response == expectedValid
        }
    }


The `postText` parameter represents the response from the ReCaptcha server. Here are examples of simulating success and failure results:

	when:"A successful response message"
    def answer = """{ "success": true }"""

    then:
    buildAndCheckAnswer(answer, true)

    when:"A failure response message"
    answer = """{ "success": false }"""

    then:
    buildAndCheckAnswer(answer, false)


# Contributing

![Target Branch](https://img.shields.io/badge/target%20branch-version1__7__0-green.svg?style=flat-square)

Contributions are welcome, but there a couple of guidelines that will make everything easier.

## Before Submission

* If you are fixing a defect, write a unit test that demonstrates the issue.
* If you are adding functionality, add appropriate unit tests. 
* Make sure that all unit tests pass.
* Ideally, make sure that the plugin still works as expected in a "real" application.

## When Submitting

* Be sure to submit pull requests against the current "target branch". This will ensure that changes are applied to the correct version.
* Make sure that commits have descriptive text that clearly explains the change. (See http://chris.beams.io/posts/git-commit/ for tips on writing good commit messages.)
* Reference appropriate issues or pull requests if needed. (Use `refs XXX` instead of `fixes XXX` or `closes XXX`)

## Why I might reject your changes

There are several reasons I might reject a code submission:

* Code does not compile, or tests do not pass
* No new unit tests (where applicable)
* Code coverage decreases
* The plugin does not behave correctly

I will probably argue with you if:

* I see no benefit in the proposed change
* The commit or PR text is not descriptive
* I disagree with the coding style


# Misc.


## CHANGELOG (PRE 0.6.0)

* 0.5.3
    * Add the ability to force a different language to be displayed.
* 0.5.1 & 0.5.2
    * Update to use the new ReCaptcha URLs.
* 0.5.0
    * Add Mailhide support.
    * Add support for specifying configuration options elsewhere than `RecaptchaConfig.groovy` via the `grails.config.locations` method.
* 0.4.5
    * Add code to perform the ReCaptcha functionality - removed recaptcha4j library.
    * Don't add captcha instance to session to avoid serialization issues.
    * Hopefully make it easier to test against.
* 0.4
    * New version number for Grails 1.1. Same functionality as 0.3.2
* 0.3.2
    * Moved code into packages.
    * Tried to make licensing easier to discern.
    * Updated to Grails 1.0.4
* 0.3
    * Added support for environments and new `<recaptcha:ifFailed>` tag.
    * Updated to Grails 1.0.3
* 0.2
    * initial release, developed and tested against Grails 1.0.2

## Thanks

* The `recaptcha-quickstart` script was borrowed heavily from the [Spring Security Core plugin](http://grails.org/plugin/spring-security-core).


# Suggestions or Comments

Feel free to submit questions through GitHub or to StackOverflow. (The Grails mailing list appears to be defunct.)

Alternatively you can contact me directly - cjohnston at megatome dot com
