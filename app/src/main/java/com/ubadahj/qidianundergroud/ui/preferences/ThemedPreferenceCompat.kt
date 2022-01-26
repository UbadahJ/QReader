package com.ubadahj.qidianundergroud.ui.preferences

import android.content.Context
import android.view.ContextThemeWrapper
import androidx.preference.PreferenceFragmentCompat
import com.ubadahj.qidianundergroud.R


abstract class ThemedPreferenceCompat : PreferenceFragmentCompat() {

    override fun getContext(): Context? {
        return ContextThemeWrapper(super.getContext(), R.style.PreferenceTheme)
    }

}