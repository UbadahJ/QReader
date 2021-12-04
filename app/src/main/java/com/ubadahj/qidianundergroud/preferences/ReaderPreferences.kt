package com.ubadahj.qidianundergroud.preferences

import android.content.SharedPreferences
import javax.inject.Inject

class ReaderPreferences @Inject constructor(
    private val preferences: SharedPreferences
) {

    var fontScale: Float
        get() = preferences.getFloat("fontScale", 1.0f)
        set(value) {
            preferences.edit().putFloat("fontScale", value).apply()
        }

}