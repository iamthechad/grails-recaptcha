package com.megatome.grails.recaptcha.net

/**
 * Copyright 2010-2014 Megatome Technologies
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
    String url
    QueryString queryString = new QueryString()
    URLConnection connection
    String text

    String getText() {
        def thisUrl = new URL(url)
        connection = thisUrl.openConnection()
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
        return connection.content.text
    }

    String toString() {
        return "POST:\n" +
                url + "\n" +
                queryString.toString()
    }
}