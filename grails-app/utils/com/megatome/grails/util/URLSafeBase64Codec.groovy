package com.megatome.grails.util

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

class URLSafeBase64Codec {
    /**
     * Simple encoding required by the Mailhide API to make the Base64 string URL safe
     */
    static encode = { target ->
        if (target == null) {
            return target
        }

        String firstPass = target.encodeAsBase64()
        firstPass.replaceAll("\\+", "-").replaceAll("/", "_")
    }
}
