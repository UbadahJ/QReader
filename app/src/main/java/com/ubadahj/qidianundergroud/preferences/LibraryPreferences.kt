package com.ubadahj.qidianundergroud.preferences

import android.content.Context
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.ubadahj.qidianundergroud.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class LibraryPreferences @Inject constructor(
    @ApplicationContext context: Context,
    preferences: FlowSharedPreferences
) {

    val columnCount = preferences.getInt(
        context.getString(R.string.pref_column_count), 2
    )

    private val updateFreqOptions = mapOf(
        "0" to null,
        "1" to (12L to TimeUnit.HOURS),
        "2" to (1L to TimeUnit.DAYS),
        "3" to (2L to TimeUnit.DAYS),
        "4" to (3L to TimeUnit.DAYS),
        "5" to (7L to TimeUnit.DAYS),
    )
    val updateFrequency = preferences.getString(
        context.getString(R.string.pref_library_update_freq), "1"
    ).mapToEntries(updateFreqOptions)

}