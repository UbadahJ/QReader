package com.ubadahj.qidianundergroud.preferences

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.ubadahj.qidianundergroud.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppearancePreferences @Inject constructor(
    @ApplicationContext context: Context,
    preferences: FlowSharedPreferences
) {

    val nightMode = preferences.getString(
        context.getString(R.string.pref_night_mode), ""
    )

}