package com.ubadahj.qidianundergroud.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ubadahj.qidianundergroud.databinding.MenuLayoutBinding
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.utils.OnRecyclerViewItemClickListener
import com.ubadahj.qidianundergroud.utils.onItemClick

class MenuDialog(
        val adapter: MenuAdapter,
        _onClick: OnRecyclerViewItemClickListener = { _, _, _ -> }
) : BottomSheetDialogFragment() {

    private var binding: MenuLayoutBinding? = null
    var onClick = _onClick
        set(value) {
            field = value
            if (isAdded) dismiss()
        }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = MenuLayoutBinding.inflate(inflater, container, false)
        binding?.apply {
            menu.layoutManager = LinearLayoutManager(requireContext())
            menu.adapter = adapter
            menu.onItemClick(onClick)
        }
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}