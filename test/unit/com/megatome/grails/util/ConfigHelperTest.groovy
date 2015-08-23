package com.megatome.grails.util

import spock.lang.Specification

class ConfigHelperTest extends Specification {
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

    def "test non-Boolean value value"() {
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

