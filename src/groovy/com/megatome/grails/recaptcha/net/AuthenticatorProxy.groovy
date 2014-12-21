package com.megatome.grails.recaptcha.net

import static java.net.Authenticator.RequestorType.PROXY

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
