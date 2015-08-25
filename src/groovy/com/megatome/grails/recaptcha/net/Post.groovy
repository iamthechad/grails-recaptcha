package com.megatome.grails.recaptcha.net

import grails.plugins.rest.client.RestBuilder
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
 */

public class Post {
    private static final log = LogFactory.getLog(this)
    String url
    QueryParams queryParams = new QueryParams(null)
    AuthenticatorProxy proxy = null
    RestBuilder rest = null

    public Post(Map options) {
        options.each { k,v -> if (this.hasProperty(k)) { this."$k" = v} }
        if (null == rest) {
            if (proxy?.isConfigured()) {
                rest = new RestBuilder(connectTimeout: 10000, readTimeout: 1000, proxy: proxy.proxy)
            } else {
                rest = new RestBuilder(connectTimeout: 10000, readTimeout: 1000)
            }
        }
    }

    def getResponse() {
        try {
            def queryUrl = url + "?" + queryParams.toRestClientString()
            def resp = rest.post(queryUrl, queryParams.params)
            return resp.json
        } catch (Exception e) {
            def message = "Failed to connect to ${url}."
            if (proxy?.isConfigured()) {
                message += "\n\tAttempting to use proxy ${proxy.server}:${proxy.port}"
                if (proxy.username != null) {
                    message += "\n\tProxy username: ${proxy.username}. (Be sure that password is correct)"
                }
            }
            log.error(message, e)
        }
        return null
    }
}