package com.megatome.grails.recaptcha.net

public class QueryStringTest extends GroovyTestCase {

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
  }

  public void testMultipleParametersWithEmpty() {
    def qs = new QueryString()
    qs.add("param1","value")
    qs.add("param2","")
    assertEquals "param1=value", qs.toString()
  }
}