package com.ubadahj.qidianundergroud.utils

import androidx.core.text.HtmlCompat

fun String.unescapeHtml() = HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)