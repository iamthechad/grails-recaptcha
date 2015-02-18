package com.megatome.grails.mailhide.security

import com.megatome.grails.mailhide.util.StringUtils
import org.codehaus.groovy.grails.plugins.codecs.HexCodec
import grails.test.GrailsUnitTestCase

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

class MailhideEncryptionTests extends GrailsUnitTestCase {
  // Test data as supplied by Mailhide web site
  static final String PRIVATE_KEY = "deadbeefdeadbeefdeadbeefdeadbeef"

  static final String emailAddress1 = "x@example.com"
  static final byte[] encrypted1 = [0Xc0, 0X11, 0Xbb, 0X9c, 0Xe8, 0X27, 0Xb4, 0Xaa, 0X96, 0X78, 0X3a, 0X45, 0Xf6, 0Xe7, 0X15, 0X35]

  static final String emailAddress2 = "johndoe@example.com"
  static final byte[] encrypted2 = [0Xc2, 0X15, 0X88, 0Xaa, 0X4d, 0X2b, 0Xe2, 0Xea, 0Xd9, 0Xfb, 0X74, 0Xbb, 0Xcb, 0Xbb, 0X92, 0X71,
  0Xe0, 0Xbd, 0Xfc, 0X40, 0X9d, 0Xde, 0X1a, 0X40, 0X1b, 0X2e, 0Xf5, 0X13, 0X6a, 0X34, 0X1e, 0X92]

  def data = [:]

  void setUp() {
    super.setUp()
    String.metaClass.decodeHex = {
      HexCodec.decode(delegate)
    }
    data[emailAddress1] = encrypted1
    data[emailAddress2] = encrypted2
  }

  void testRoundtrip() {
    MailhideEncryption me = new MailhideEncryption()
    data.each {k, v->
      def encoded = me.encrypt(StringUtils.padString(k), PRIVATE_KEY)
      assert null != encoded
      assert v == encoded
    }
  }
}
