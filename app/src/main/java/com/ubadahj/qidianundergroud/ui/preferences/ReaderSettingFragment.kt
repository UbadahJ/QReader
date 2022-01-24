package com.ubadahj.qidianundergroud.ui.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ubadahj.qidianundergroud.R

class ReaderSettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.reader_preferences, rootKey)
    }
}