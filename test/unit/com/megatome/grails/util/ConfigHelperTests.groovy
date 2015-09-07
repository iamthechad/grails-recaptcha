package com.megatome.grails.util

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

import spock.lang.Specification

class ConfigHelperTests extends Specification {
    def "test null value"() {
        when:
        def value = ConfigHelper.booleanValue(null)

        then:
        !value
    }

    def "test Boolean value"() {
        when:
        def value = ConfigHelper.booleanValue(Boolean.TRUE)

        then:
        value

        when:
        value = ConfigHelper.booleanValue(Boolean.FALSE)

        then:
        !value
    }

    def "test non-Boolean value"() {
        when:
        def value = ConfigHelper.booleanValue("true")

        then:
        value

        when:
        value = ConfigHelper.booleanValue("false")

        then:
        !value
    }
}

