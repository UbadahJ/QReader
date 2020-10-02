package com.ubadahj.qidianundergroud.utils

import android.webkit.WebView
import kotlinx.coroutines.delay

suspend fun WebView.getHtml(): String {
    var contents: String? = null
    if (!settings.javaScriptEnabled)
        throw IllegalStateException("Javascript is disabled")

    evaluateJavascript(
        "(function() {\n" +
                "    return (\n" +
                "        '<html>' +\n" +
                "        document.getElementsByTagName('html')[0].innerHTML +\n" +
                "        '</html>'\n" +
                "    );\n" +
                "})();"
    ) { contents = it }

    while (contents == null)
        delay(200)

    return contents!!.replace("\\u003C", "<")
        .replace("\\n", "")
        .replace("\\t", "")
        .replace("\\\"", "\"")
        .replace("<hr />", "")
}