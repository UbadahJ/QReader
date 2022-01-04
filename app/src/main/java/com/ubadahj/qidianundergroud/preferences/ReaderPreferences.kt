package com.ubadahj.qidianundergroud.preferences

import android.content.SharedPreferences
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import javax.inject.Inject

class ReaderPreferences @Inject constructor(
    preferences: SharedPreferences
) {

    private val flowPreferences = FlowSharedPreferences(preferences)

    val fontScale = flowPreferences.getFloat("fontScale", 1.0f)

}