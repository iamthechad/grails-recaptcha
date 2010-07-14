package com.megatome.grails.mailhide.security

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec

class MailhideEncryption {
  static def encrypt(def string, def key) {
    Cipher cipher = setupCipher(Cipher.ENCRYPT_MODE, key)
    cipher.doFinal(string.getBytes())
  }

  static decode = { str ->
    "bar"
  }

  private static byte[] hexStringToByteArray(String encoded) {
    byte[] result = new byte[encoded.length()/2];
    char[] enc = encoded.toCharArray();
    for (int i = 0; i < enc.length; i += 2) {
        StringBuilder curr = new StringBuilder(2);
        curr.append(enc[i]).append(enc[i + 1]);
        result[i/2] = Integer.parseInt(curr.toString(), 16) as byte;
    }
    return result;
  }

  private static setupCipher(mode, key) {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

        // setup key
        //byte[] keyBytes = new byte[16];
        //byte[] b = password.getBytes("UTF-8");
        //println b
        //println hexStringToByteArray(password)
        //println b.encodeAsHex()
        //int len = b.length;
        //if (len > keyBytes.length)
        //      len = keyBytes.length;
        //System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

        byte[] iv = new byte[16]
        iv.each {
          it = 0X00
        }
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(mode, keySpec, ivSpec);
        return cipher
    }
}
