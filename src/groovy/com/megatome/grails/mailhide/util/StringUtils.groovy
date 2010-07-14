package com.megatome.grails.mailhide.util

class StringUtils {
  public static final int DEFAULT_BLOCK_SIZE = 16

  static def padString(def str) {
    padString (str, DEFAULT_BLOCK_SIZE)
  }

  static def padString(def str, def blockSize) {
    def numpad = blockSize - (str.size() % blockSize)
    def padded = str
    (1..numpad).each {
      padded += (numpad as char)
    }
    padded
  }
}
