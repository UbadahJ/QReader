package com.ubadahj.qidianundergroud.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.ubadahj.qidianundergroud.databinding.ReaderContainerLayoutBinding
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.preferences.ReaderPreferences
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.ui.adapters.diff.GroupDiffCallback
import com.ubadahj.qidianundergroud.ui.adapters.generic.DiffFragmentStateAdapter
import com.ubadahj.qidianundergroud.ui.viewmodels.MainViewModel
import com.ubadahj.qidianundergroud.utils.ui.showSystemBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReaderContainerFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: ReaderContainerLayoutBinding? = null
    private val adapter by lazy { ChapterReaderAdapter(this) }

    @Inject
    lateinit var groupRepo: GroupRepository

    @Inject
    lateinit var preferences: ReaderPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ReaderContainerLayoutBinding.inflate(inflater, container, false)
            .apply { binding = this }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.pager?.adapter = adapter

        lifecycleScope.launch {
            groupRepo.getGroups(viewModel.selectedBook.value!!).first().let {
                adapter.submitList(it)
                viewModel.selectedGroup.value?.let { group ->
                    binding?.pager?.setCurrentItem(
                        adapter.currentList.indexOf(group),
                        false
                    )
                }
            }
        }

        lifecycleScope.launch {
            launch {
                preferences.immersiveMode.asFlow()
                    .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                    .collect {
                        requireActivity().showSystemBar(!it)
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        requireActivity().showSystemBar(true)
    }

    private class ChapterReaderAdapter(
        fragment: Fragment
    ) : DiffFragmentStateAdapter<Group>(fragment, GroupDiffCallback) {
        override fun createFragment(position: Int): Fragment {
            return ChapterFragment.newInstance(currentList[position].link)
        }
    }

}