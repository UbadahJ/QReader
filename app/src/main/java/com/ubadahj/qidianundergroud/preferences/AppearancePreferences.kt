package com.ubadahj.qidianundergroud.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.ubadahj.qidianundergroud.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppearancePreferences @Inject constructor(
    @ApplicationContext context: Context,
    preferences: FlowSharedPreferences
) {

    private val nightModeOptions = mapOf(
        "0" to AppCompatDelegate.MODE_NIGHT_NO,
        "1" to AppCompatDelegate.MODE_NIGHT_YES,
        "2" to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    )
    val nightMode = preferences.getString(
        context.getString(R.string.pref_night_mode), ""
    )

    fun nightModeMapper(option: String) = nightModeOptions[option] ?: nightModeOptions["2"]!!

}