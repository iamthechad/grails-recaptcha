package com.megatome.grails.recaptcha.net

import grails.plugins.rest.client.RestBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers
import org.springframework.test.web.client.response.MockRestResponseCreators
import spock.lang.Specification

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

class PostTests extends Specification {
    def "Test basic POST"(){
        given:"A rest client instance"
        def rest = new RestBuilder()
        final mockServer = MockRestServiceServer.createServer(rest.restTemplate)
        mockServer.expect(MockRestRequestMatchers.requestTo("http://www.google.com"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess('{"success":"true"}', MediaType.APPLICATION_JSON))
        def post = new Post(url: "http://www.google.com", rest: rest)

        when:
        def resp = post.response

        then:
        mockServer.verify()
        resp
    }

    def "Test proxy configuration"() {
        when:
        def authProxy = new AuthenticatorProxy(server: "localhost", port: 8080)
        def post = new Post(url: "http://www.google.com", proxy: authProxy)
        def proxyAddress = post.rest.restTemplate.requestFactory?.@proxy?.address()

        then:"The proxy settings are correct"
        proxyAddress != null
        proxyAddress.hostName == "localhost"
        proxyAddress.port == 8080
    }

    def "Test with illegal arguments"() {
        when:
        def post = new Post(url: "http://www.google.com", foo:"bar")

        then:
        post.url == "http://www.google.com"
    }

    def "Test illegal URL"() {
        when:
        def post = new Post(url: "abc123")

        then:
        post.response == null

        when:
        def authProxy = new AuthenticatorProxy(server: "localhost", port: 8080, username: "foo", password: "bar")
        post = new Post(url: "abc123", proxy: authProxy)

        then:
        post.response == null

        when:
        authProxy = new AuthenticatorProxy(server: "localhost", port: 8080)
        post = new Post(url: "abc123", proxy: authProxy)

        then:
        post.response == null
    }
}
