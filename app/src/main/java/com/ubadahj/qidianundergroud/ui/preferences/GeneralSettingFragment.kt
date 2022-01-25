package com.ubadahj.qidianundergroud.ui.preferences

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ubadahj.qidianundergroud.R

class GeneralSettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.general_preferences, rootKey)
        find(R.string.pkey_open_reader)?.setOnPreferenceClickListener {
            findNavController().navigate(
                GeneralSettingFragmentDirections.actionToReaderSettingFragment()
            )
            true
        }
    }

    private fun find(@StringRes key: Int) =
        findPreference<Preference>(requireContext().getString(key))


}