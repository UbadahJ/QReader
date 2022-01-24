package com.ubadahj.qidianundergroud.preferences

import android.content.Context
import android.content.SharedPreferences
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.ubadahj.qidianundergroud.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReaderPreferences @Inject constructor(
    @ApplicationContext context: Context,
    preferences: SharedPreferences
) {

    private val flowPreferences = FlowSharedPreferences(preferences)

    val immersiveMode = flowPreferences.getBoolean(
        context.getString(R.string.pref_immersive_mode), true
    )

    val fontScale = flowPreferences.getInt(
        context.getString(R.string.pref_scale_factor), 10
    )

    val lockFontScale = flowPreferences.getBoolean(
        context.getString(R.string.pref_lock_font_scale), false
    )

}