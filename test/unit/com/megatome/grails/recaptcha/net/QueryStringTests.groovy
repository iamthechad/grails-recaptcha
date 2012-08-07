package com.megatome.grails.recaptcha.net

/**
 * Copyright 2010 Megatome Technologies
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

public class QueryStringTests extends GroovyTestCase {

  public void testCreate() {
    def qs = new QueryString()
    assertEquals "", qs.toString()
  }

  public void testAddParameter() {
    def qs = new QueryString()
    qs.add("param", "value")
    assertEquals "param=value", qs.toString()
  }

  public void testMultipleParameters() {
    def qs = new QueryString()
    qs.add("param1","value")
    qs.add("param2","value")
    assertEquals "param1=value&param2=value", qs.toString()
  }

  public void testMultipleParametersWithNull() {
    def qs = new QueryString()
    qs.add("param1","value")
    qs.add("param2",null)
    assertEquals "param1=value", qs.toString()
	qs = new QueryString([param1:'value', param2:null])
    assertEquals "param1=value", qs.toString()
  }

  public void testMultipleParametersWithEmpty() {
    def qs = new QueryString()
    qs.add("param1","value")
    qs.add("param2","")
    assertEquals "param1=value", qs.toString()
  }
}