package com.ubadahj.qidianundergroud.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import coil.loadAny
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ubadahj.qidianundergroud.databinding.InputDialogBinding

data class InputBottomSheetConfig(
    val title: String,
    val description: String,
    val icon: Any,
    val action: (String) -> Unit,
    val buttonText: String
)

class InputBottomSheet(
    private val config: InputBottomSheetConfig
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return InputDialogBinding.inflate(inflater, container, false).apply {
            inputDialogText.text = config.title
            inputDialogDesc.text = config.description
            inputDialogAction.text = config.buttonText
            inputDialogImage.loadAny(config.icon)
            inputDialogAction.setOnClickListener {
                val text = inputDialogEdit.text
                if (!text.isNullOrBlank()) {
                    dismiss()
                    config.action(text.toString())
                }
            }
        }.root
    }

}