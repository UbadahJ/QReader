package com.ubadahj.qidianundergroud.preferences

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.ubadahj.qidianundergroud.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ReaderPreferences @Inject constructor(
    @ApplicationContext context: Context,
    preferences: FlowSharedPreferences
) {

    val immersiveMode = preferences.getBoolean(
        context.getString(R.string.pref_immersive_mode), true
    )

    val fontScale = preferences.getInt(
        context.getString(R.string.pref_scale_factor), 10
    )

    val lineSpacingMultiplier = preferences.getInt(
        context.getString(R.string.pref_line_spacing_multiplier), 4
    )

    val lockFontScale = preferences.getBoolean(
        context.getString(R.string.pref_lock_font_scale), false
    )

}