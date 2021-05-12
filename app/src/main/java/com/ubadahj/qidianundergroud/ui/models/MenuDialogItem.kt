package com.ubadahj.qidianundergroud.ui.models

import androidx.annotation.DrawableRes

data class MenuDialogItem(
    val text: String,
    @DrawableRes val icon: Int = 0,
    val onClick: (() -> Unit)? = null
) {
    constructor(text: String, onClick: (() -> Unit)) : this(text, 0, onClick)
}
