package com.megatome.grails.mailhide.security

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec

class MailhideEncryption {
  static def encrypt(def string, def key) {
    Cipher cipher = setupCipher(Cipher.ENCRYPT_MODE, key)
    cipher.doFinal(string.getBytes())
  }

  private static setupCipher(mode, key) {
        def cipher = Cipher.getInstance("AES/CBC/NoPadding")

        def keyBytes = key.decodeHex()
        def keySpec = new SecretKeySpec(keyBytes, "AES")

        byte[] iv = ([0] * 16) as byte[]
        def ivSpec = new IvParameterSpec(iv)
        cipher.init(mode, keySpec, ivSpec)
        cipher
    }
}
