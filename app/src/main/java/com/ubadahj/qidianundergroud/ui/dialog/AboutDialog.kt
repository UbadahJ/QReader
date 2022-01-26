package com.ubadahj.qidianundergroud.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ubadahj.qidianundergroud.BuildConfig
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.AboutFragmentBinding
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.ui.openLink

class AboutDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return AboutFragmentBinding.inflate(inflater, container, false).apply {
            aboutVersion.text = BuildConfig.VERSION_NAME
            recyclerView.adapter = MenuAdapter(listOf(
                MenuDialogItem("Show on Github", R.drawable.github) {
                    requireActivity().openLink("https://github.com/UbadahJ/QReader")
                }
            ))
        }.root
    }

}