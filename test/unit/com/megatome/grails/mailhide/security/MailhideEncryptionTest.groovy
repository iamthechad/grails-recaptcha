package com.megatome.grails.mailhide.security

import com.megatome.grails.mailhide.util.StringUtils
import org.codehaus.groovy.grails.plugins.codecs.HexCodec

class MailhideEncryptionTest extends GroovyTestCase {
  private static String PRIVATE_KEY = "deadbeefdeadbeefdeadbeefdeadbeef"
  private static byte[] encrypted = [0Xc0, 0X11, 0Xbb, 0X9c, 0Xe8, 0X27, 0Xb4, 0Xaa, 0X96, 0X78, 0X3a, 0X45, 0Xf6, 0Xe7, 0X15, 0X35]
  static def expected = 0Xc011bb9ce827b4aa96783a45f6e71535 as String
  
  void testRoundtrip() {
        MailhideEncryption me = new MailhideEncryption()
        def original = "x@example.com"
        def encoded = me.encrypt(StringUtils.padString(original), HexCodec.decode(PRIVATE_KEY))
        assert null != encoded
        println HexCodec.encode(encrypted)
        println HexCodec.decode(PRIVATE_KEY)
        println hexdump(encrypted)
        println hexdump(encoded)
        assert encrypted == encoded        
        //def decoded = encoded.decodeAES()

        //assert original != encoded
        //assert encoded != decoded
        //assert original == decoded
    }

  private def hexdump(bytes) {
    def h = new java.math.BigInteger(1, bytes).toString(16)
    return bytes.size() ? h.length() & 1 ? "0" + h : h : ""
  }
}
