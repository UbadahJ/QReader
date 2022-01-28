package com.ubadahj.qidianundergroud.preferences

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.ubadahj.qidianundergroud.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkPreferences @Inject constructor(
    @ApplicationContext context: Context,
    preferences: FlowSharedPreferences
) {

    val useProxy = preferences.getBoolean(
        context.getString(R.string.pref_use_proxy), true
    )

    val disableWebViewCache = preferences.getBoolean(
        context.getString(R.string.pref_disable_webview_cache), false
    )

    val requestTimeout = preferences.getString(
        context.getString(R.string.pref_webview_request_timeout), "8000"
    ).map(String::toLong, Long::toString)

    val concurrentRequests = preferences.getString(
        context.getString(R.string.pref_concurrent_requests), "12"
    ).map(String::toInt, Int::toString)

    val connectionTimeout = preferences.getString(
        context.getString(R.string.pref_req_connection_timeout), "2"
    ).map(String::toLong, Long::toString)

    val readTimeout = preferences.getString(
        context.getString(R.string.pref_req_read_timeout), "2"
    ).map(String::toLong, Long::toString)

    val writeTimeout = preferences.getString(
        context.getString(R.string.pref_req_write_timeout), "2"
    ).map(String::toLong, Long::toString)

    val maxHostRequests = preferences.getString(
        context.getString(R.string.pref_req_max_host_requests), "10"
    ).map(String::toInt, Int::toString)

    val maxRequests = preferences.getString(
        context.getString(R.string.pref_req_max_requests), "64"
    ).map(String::toInt, Int::toString)

}