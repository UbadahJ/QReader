package com.ubadahj.qidianundergroud.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mikepenz.fastadapter.FastAdapter
import com.ubadahj.qidianundergroud.databinding.MenuLayoutBinding
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter

class MenuDialog(menuAdapter: MenuAdapter) : BottomSheetDialogFragment() {

    private var binding: MenuLayoutBinding? = null
    val adapter = FastAdapter.with(menuAdapter)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MenuLayoutBinding.inflate(inflater, container, false)
        binding?.apply {
            menu.layoutManager = LinearLayoutManager(requireContext())
            menu.adapter = adapter
        }
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}