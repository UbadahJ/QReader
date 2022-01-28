package com.ubadahj.qidianundergroud.ui.preferences

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import com.ubadahj.qidianundergroud.R

class NetworkSettingFragment : ThemedPreferenceCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.network_preferences, rootKey)

        val concurrentRequests: EditTextPreference? = get(R.string.pref_concurrent_requests)

        val connectionTimeout: EditTextPreference? = get(R.string.pref_req_connection_timeout)
        val readTimeout: EditTextPreference? = get(R.string.pref_req_read_timeout)
        val writeTimeout: EditTextPreference? = get(R.string.pref_req_write_timeout)
        val maxHostRequests: EditTextPreference? = get(R.string.pref_req_max_host_requests)
        val maxRequests: EditTextPreference? = get(R.string.pref_req_max_requests)

        val webViewTimeout: EditTextPreference? = get(R.string.pref_webview_request_timeout)

        webViewTimeout?.setProvider { "${it.text} ms" }
        listOf(connectionTimeout, readTimeout, writeTimeout).forEach { pref ->
            pref?.setProvider { "${it.text} minutes" }
        }

        listOf(
            concurrentRequests,
            webViewTimeout,
            connectionTimeout,
            readTimeout,
            writeTimeout,
            maxHostRequests,
            maxRequests
        ).forEach { pref ->
            pref?.setOnBindEditTextListener { it.inputType = InputType.TYPE_CLASS_NUMBER }
        }
    }
}