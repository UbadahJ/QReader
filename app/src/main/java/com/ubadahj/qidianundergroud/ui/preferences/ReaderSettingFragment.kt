package com.ubadahj.qidianundergroud.ui.preferences

import android.os.Bundle
import com.ubadahj.qidianundergroud.R

class ReaderSettingFragment : ThemedPreferenceCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.reader_preferences, rootKey)
    }
}