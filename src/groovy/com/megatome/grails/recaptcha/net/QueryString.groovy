package com.megatome.grails.recaptcha.net

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

public class QueryString {
    Map params = [:]
//this constructor allows you to pass in a Map
    QueryString(Map params) {
        if (params) {
            this.params.putAll(params)
        }
    }
//this method allows you to add name/value pairs
    void add(String name, Object value) {
        if (value) {
            params.put(name, value)
        }
    }
//this method returns a well-formed QueryString
    String toString() {
        def list = []
        params.each {name, value ->
            list << "$name=" + URLEncoder.encode(value.toString())
        }
        return list.join("&")
    }
}