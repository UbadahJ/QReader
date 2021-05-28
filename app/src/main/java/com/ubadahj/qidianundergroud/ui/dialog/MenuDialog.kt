package com.ubadahj.qidianundergroud.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ubadahj.qidianundergroud.databinding.MenuLayoutBinding
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter

class MenuDialog(
    val adapter: MenuAdapter
) : BottomSheetDialogFragment() {

    private var binding: MenuLayoutBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return MenuLayoutBinding.inflate(inflater, container, false).apply {
            binding = this
            menu.layoutManager = LinearLayoutManager(requireContext())
            menu.adapter = adapter.apply { postOnClick = { dismiss() } }
        }.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
