package com.megatome.grails.util

import org.codehaus.groovy.grails.plugins.codecs.Base64Codec

/**
 * Copyright 2010-2012 Megatome Technologies
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

class URLSafeBase64CodecTests extends GroovyTestCase {
  static final byte[] input1 = [0Xc0, 0X11, 0Xbb, 0X9c, 0Xe8, 0X27, 0Xb4, 0Xaa, 0X96, 0X78, 0X3a, 0X45, 0Xf6, 0Xe7, 0X15, 0X35]
  static final byte[] input2 = [0Xc2, 0X15, 0X88, 0Xaa, 0X4d, 0X2b, 0Xe2, 0Xea, 0Xd9, 0Xfb, 0X74, 0Xbb, 0Xcb, 0Xbb, 0X92, 0X71,
          0Xe0, 0Xbd, 0Xfc, 0X40, 0X9d, 0Xde, 0X1a, 0X40, 0X1b, 0X2e, 0Xf5, 0X13, 0X6a, 0X34, 0X1e, 0X92]

  static final String expected1 = "wBG7nOgntKqWeDpF9ucVNQ=="
  static final String expected2 = "whWIqk0r4urZ-3S7y7uSceC9_ECd3hpAGy71E2o0HpI="

  def data = [:]

  void setUp() {
    super.setUp()
    byte[].metaClass.encodeAsBase64 = {
      Base64Codec.encode(delegate)
    }
    data[input1] = expected1
    data[input2] = expected2
  }

  void testEncode() {
    data.each { k, v ->
      def safeBase64Data = URLSafeBase64Codec.encode(k)
      assert null != safeBase64Data
      assert v == safeBase64Data
    }
  }
}
