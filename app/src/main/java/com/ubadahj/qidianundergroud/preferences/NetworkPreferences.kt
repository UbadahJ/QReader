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

    val requestTimeout = preferences.getString(
        context.getString(R.string.pref_request_timeout), "8000"
    )

}