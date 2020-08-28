package com.ubadahj.qidianundergroud.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.LibraryFragmentBinding

class LibraryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: LibraryFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LibraryFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar.appbar)
            toolbar.appbar.title = resources.getText(R.string.library)
            floatingButton.setOnClickListener {
                findNavController().navigate(
                    LibraryFragmentDirections.actionLibraryFragmentToBrowseFragment()
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}