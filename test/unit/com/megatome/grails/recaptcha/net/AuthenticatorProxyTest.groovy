package com.megatome.grails.recaptcha.net

import spock.lang.Specification

class AuthenticatorProxyTest extends Specification {
    def setup() {
        Authenticator.default = null
    }

    def "test proxy with no params"() {
        when:
        def authProxy = new AuthenticatorProxy(null)

        then:
        !authProxy.configured
        authProxy.proxy == null

        when:
        authProxy = new AuthenticatorProxy(server: null)

        then:
        !authProxy.configured
        authProxy.proxy == null

        when:
        authProxy = new AuthenticatorProxy(server: "")

        then:
        !authProxy.configured
        authProxy.proxy == null
    }

    def "test proxy with server param and default port"() {
        when:
        new AuthenticatorProxy(server: "localhost")

        then:
        def exception = thrown(IllegalArgumentException)
        exception.message.contains("port out of range")
    }

    def "test proxy with server and port"() {
        setup:
        def authProxy = new AuthenticatorProxy(server: "localhost", port: 8080)

        expect:
        authProxy.configured
        authProxy.proxy != null
        authProxy.server == "localhost"
        authProxy.port == 8080
        authProxy.username == null
        authProxy.password == null
    }

    def "test proxy with invalid params"() {
        setup:
        def authProxy = new AuthenticatorProxy(server: "localhost", port: 8080, foo: "bar")

        expect:
        authProxy.configured
        authProxy.proxy != null
        authProxy.server == "localhost"
        authProxy.port == 8080
        authProxy.username == null
        authProxy.password == null
    }

    def "test proxy with username only"() {
        when:
        def authProxy = new AuthenticatorProxy(server: "localhost", port: 8080, username: "user")

        then:
        authProxy.configured
        authProxy.proxy != null
        authProxy.server == "localhost"
        authProxy.port == 8080
        authProxy.username == "user"
        authProxy.password == null

        when:
        def passwordAuthentication = Authenticator.requestPasswordAuthentication("localhost", null, 8080, "http", "prompt", "HTTP", null, Authenticator.RequestorType.PROXY);

        then:
        passwordAuthentication == null
    }

    def "test proxy with username and password"() {
        when:
        def authProxy = new AuthenticatorProxy(server: "localhost", port: 8080, username: "user", password: "password")

        then:
        authProxy.configured
        authProxy.proxy != null
        authProxy.server == "localhost"
        authProxy.port == 8080
        authProxy.username == "user"
        authProxy.password == "password"

        when:
        def passwordAuthentication = Authenticator.requestPasswordAuthentication("localhost", null, 8080, "http", "prompt", "HTTP", null, Authenticator.RequestorType.PROXY);

        then:
        passwordAuthentication != null
        passwordAuthentication.userName == "user"

        when:
        passwordAuthentication = Authenticator.requestPasswordAuthentication("somehost", null, 8080, "http", "prompt", "HTTP", null, Authenticator.RequestorType.PROXY);

        then:
        passwordAuthentication == null

        when:
        passwordAuthentication = Authenticator.requestPasswordAuthentication("localhost", null, 1234, "http", "prompt", "HTTP", null, Authenticator.RequestorType.PROXY);

        then:
        passwordAuthentication == null

        when:
        passwordAuthentication = Authenticator.requestPasswordAuthentication("localhost", null, 8080, "http", "prompt", "HTTP", null, Authenticator.RequestorType.SERVER);

        then:
        passwordAuthentication == null
    }
}
