package com.megatome.grails.mailhide.security

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec

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

class MailhideEncryption {
    static def encrypt(def string, String key) {
        Cipher cipher = setupCipher(Cipher.ENCRYPT_MODE, key)
        cipher.doFinal(string.getBytes())
    }

    private static setupCipher(mode, String key) {
        def cipher = Cipher.getInstance("AES/CBC/NoPadding")

        def keyBytes = key.decodeHex()
        def keySpec = new SecretKeySpec(keyBytes, "AES")

        byte[] iv = ([0] * 16) as byte[]
        def ivSpec = new IvParameterSpec(iv)
        cipher.init(mode, keySpec, ivSpec)
        cipher
    }
}
