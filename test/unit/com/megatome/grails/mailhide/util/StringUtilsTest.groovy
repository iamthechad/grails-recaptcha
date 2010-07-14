package com.megatome.grails.mailhide.util

class StringUtilsTest extends GroovyTestCase {
  void testPadString() {
    def testStr = ["x@example.com", "johndoe@example.com"]
    testStr.each { nextStr ->
      def padded = StringUtils.padString(nextStr)
      assert null != padded
      assert padded.size() % 16 == 0
      def padChars = padded[nextStr.size()..padded.size() - 1]
      def numpad = padChars.size()
      padChars.getChars().each {
        assert numpad == it
      }
    }
  }
}
