package com.megatome.grails.recaptcha.net

import grails.plugins.rest.client.RestBuilder
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import spock.lang.Specification

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

class PostTest extends Specification {
    def "Test basic POST"(){
        given:"A rest client instance"
        def rest = new RestBuilder()
        final mockServer = MockRestServiceServer.createServer(rest.restTemplate)
        mockServer.expect(requestTo("http://www.google.com"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess('{"success":"true"}', MediaType.APPLICATION_JSON))
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
