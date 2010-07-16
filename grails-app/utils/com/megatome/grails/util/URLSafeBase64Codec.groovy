package com.megatome.grails.util

class URLSafeBase64Codec {
  static encode = { target ->
    if (target == null) {
      return target
    }

    String firstPass = target.encodeAsBase64()
    firstPass.replaceAll("\\+", "-").replaceAll("/", "_")
  }
}
