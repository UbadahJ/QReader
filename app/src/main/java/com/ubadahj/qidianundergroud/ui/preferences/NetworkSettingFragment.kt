package com.ubadahj.qidianundergroud.ui.preferences

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import com.ubadahj.qidianundergroud.R

class NetworkSettingFragment : ThemedPreferenceCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.network_preferences, rootKey)

        findPreference<EditTextPreference>(
            requireContext()
                .getString(R.string.pref_request_timeout)
        )
            ?.setOnBindEditTextListener {
                it.inputType = InputType.TYPE_CLASS_NUMBER
            }
    }
}