package com.ubadahj.qidianundergroud.ui.preferences

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import com.ubadahj.qidianundergroud.R

class GeneralSettingFragment : ThemedPreferenceCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.general_preferences, rootKey)

        setNavigation(
            R.string.pkey_open_library,
            GeneralSettingFragmentDirections.actionToLibrarySettingFragment()
        )
        setNavigation(
            R.string.pkey_open_reader,
            GeneralSettingFragmentDirections.actionToReaderSettingFragment()
        )
        setNavigation(
            R.string.pkey_open_network,
            GeneralSettingFragmentDirections.actionToNetworkSettingFragment()
        )
        setNavigation(
            R.string.pkey_open_appearance,
            GeneralSettingFragmentDirections.actionToAppearanceSettingFragment()
        )
    }

    private fun setNavigation(@StringRes key: Int, direction: NavDirections) =
        findPreference<Preference>(requireContext().getString(key))
            ?.setOnPreferenceClickListener {
                findNavController().navigate(direction)
                true
            }

    private fun find(@StringRes key: Int) =
        findPreference<Preference>(requireContext().getString(key))


}