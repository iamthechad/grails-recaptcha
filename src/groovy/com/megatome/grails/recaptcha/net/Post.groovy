package com.megatome.grails.recaptcha.net

import groovy.json.JsonSlurper
import org.apache.commons.logging.LogFactory

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
 *
 * Based on Groovy Recipes
 */

public class Post {
    private static final log = LogFactory.getLog(this)
    String url
    QueryString queryString = new QueryString()
    URLConnection connection
    String text
    AuthenticatorProxy proxy = null

    def getResponse() {
        try {
            def thisUrl = new URL(url)
            connection = null
            if (proxy.isConfigured()) {
                connection = thisUrl.openConnection(proxy.getProxy())
            } else {
                connection = thisUrl.openConnection()
            }
            if (connection.metaClass.respondsTo(connection, "setReadTimeout", int)) {
                connection.readTimeout = 10000
            }
            if (connection.metaClass.respondsTo(connection, "setConnectTimeout", int)) {
                connection.connectTimeout = 10000
            }
            connection.setRequestMethod("POST")
            connection.doOutput = true
            Writer writer = new OutputStreamWriter(connection.outputStream)
            writer.write(queryString.toString())
            writer.flush()
            writer.close()
            connection.connect()
            return new JsonSlurper().parseText(connection.content.text)
        } catch (Exception e) {
            def message = "Failed to connect to ${url}."
            if (proxy.isConfigured()) {
                message += "\n\tAttempting to use proxy ${proxy.server}:${proxy.port}"
                if (proxy.username != null) {
                    message += "\n\tProxy username: ${proxy.username}. (Be sure that password is correct)"
                }
            }
            log.error(message, e)
        }
        return null
    }

    String toString() {
        return "POST:\n" +
                url + "\n" +
                queryString.toString()
    }
}