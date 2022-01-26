package com.ubadahj.qidianundergroud.ui.preferences

import android.os.Bundle
import com.ubadahj.qidianundergroud.R

class AppearanceSettingFragment : ThemedPreferenceCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.appearance_preferences, rootKey)
    }
}