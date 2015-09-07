package com.megatome.grails.recaptcha.net

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

class QueryParamsTests extends Specification {

  def "test empty constructor"() {
    setup:
    def qp = new QueryParams()

    expect:
    qp.toString() == ""
    qp.toRestClientString() == ""
  }

  def "test adding a single parameter"() {
    setup:
    def qp = new QueryParams()

    when:
    qp.add("param", "value")

    then:
    qp.toString() == "param=value"
    qp.toRestClientString() == "param={param}"
    qp.params.size() == 1
    qp.params["param"] == "value"
  }

  def "test adding multiple parameters"() {
    setup:
    def qp = new QueryParams()

    when:
    qp.add("param1","value")
    qp.add("param2","value")

    then:
    qp.toString() == "param1=value&param2=value"
    qp.toRestClientString() == "param1={param1}&param2={param2}"
    qp.params.size() == 2
    qp.params["param1"] == "value"
    qp.params["param2"] == "value"
  }

  def "test creating with multiple parameters"() {
    setup:
    def qp = new QueryParams(param1: "value", param2: "value")

    expect:
    qp.toString() == "param1=value&param2=value"
    qp.toRestClientString() == "param1={param1}&param2={param2}"
    qp.params.size() == 2
    qp.params["param1"] == "value"
    qp.params["param2"] == "value"
  }

  def "test adding invalid parameters"() {
    setup:
    def qp = new QueryParams()
    qp.add("param1", "value")

    when:
    qp.add("param2", null)

    then:
    qp.toString() == "param1=value"
    qp.toRestClientString() == "param1={param1}"
    qp.params.size() == 1
    qp.params["param1"] == "value"

    when:
    qp.add("param2", "")

    then:
    qp.toString() == "param1=value"
    qp.toRestClientString() == "param1={param1}"
    qp.params.size() == 1
    qp.params["param1"] == "value"
  }
}