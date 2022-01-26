package com.ubadahj.qidianundergroud.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.ChapterBottomSheetBinding
import com.ubadahj.qidianundergroud.ui.preferences.ReaderSettingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContentPreferencesDialog : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ChapterBottomSheetBinding.inflate(inflater, container, false).root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager
            .beginTransaction()
            .replace(R.id.standard_bottom_sheet, ReaderSettingFragment())
            .commit()
    }

}