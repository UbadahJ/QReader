package com.ubadahj.qidianundergroud.ui.preferences

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ubadahj.qidianundergroud.R


abstract class ThemedPreferenceCompat : PreferenceFragmentCompat() {

    override fun getContext(): Context? {
        return ContextThemeWrapper(super.getContext(), R.style.PreferenceTheme)
    }

    protected fun <T : Preference> get(@StringRes id: Int) = findPreference<T>(getString(id))
    protected fun <T : Preference> T.setProvider(action: (T) -> CharSequence) {
        summaryProvider = Preference.SummaryProvider<T>(action)
    }

}