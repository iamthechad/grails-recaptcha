package com.megatome.grails.recaptcha.net

import static java.net.Authenticator.RequestorType.PROXY

class AuthenticatorProxy {
    String server = null
    int port = -1
    String username = null
    String password = null
    private def proxy = null

    public AuthenticatorProxy(Map map) {
        map.each { k,v -> if (this.hasProperty(k)) { this."$k" = v} }
        if (server != null && server.length() != 0) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(server, port))

            if (username != null && password != null) {
                // Build an authenticator
                Authenticator.setDefault(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        def passwordAuth = null
                        if (getRequestorType() == PROXY) {
                            if (getRequestingHost().equalsIgnoreCase(server)) {
                                if (port == getRequestingPort()) {
                                    // Seems to be OK.
                                    passwordAuth = new PasswordAuthentication(username, password.toCharArray())
                                }
                            }
                        }
                        passwordAuth
                    }
                })
            }
        }
    }

    public boolean isConfigured() {
        return (proxy != null)
    }

    public Proxy getProxy() {
        return proxy
    }
}
