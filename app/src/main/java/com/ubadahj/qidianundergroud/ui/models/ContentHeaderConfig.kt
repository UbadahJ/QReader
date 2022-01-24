package com.ubadahj.qidianundergroud.ui.models

import com.ubadahj.qidianundergroud.models.Content

data class ContentHeaderConfig(
    val onBackPressed: () -> Unit,
    val onMenuPressed: (Content) -> Unit
)