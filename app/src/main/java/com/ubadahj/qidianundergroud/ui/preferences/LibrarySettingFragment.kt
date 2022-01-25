package com.ubadahj.qidianundergroud.ui.preferences

import android.os.Bundle
import com.ubadahj.qidianundergroud.R

class LibrarySettingFragment : ThemedPreferenceCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.library_preferences, rootKey)
    }
}