package com.ubadahj.qidianundergroud.utils

import android.webkit.WebView
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun WebView.getHtml(): String = suspendCancellableCoroutine { continuation ->
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
    ) {
        continuation.resume(
            it!!.replace("\\u003C", "<")
                .replace("\\n", "")
                .replace("\\t", "")
                .replace("\\\"", "\"")
                .replace("<hr />", "")
        )
    }
}
