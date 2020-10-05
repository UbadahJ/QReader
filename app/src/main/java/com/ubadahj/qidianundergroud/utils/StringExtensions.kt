package com.ubadahj.qidianundergroud.utils

import androidx.core.text.HtmlCompat
import java.security.MessageDigest

fun String.unescapeHtml() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

val String.md5: String
    get() {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.joinToString("") {
            "%02x".format(it)
        }
    }