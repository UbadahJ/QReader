package com.ubadahj.qidianundergroud.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ubadahj.qidianundergroud.databinding.ReaderContainerLayoutBinding
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.ui.adapters.diff.GroupDiffCallback
import com.ubadahj.qidianundergroud.ui.adapters.generic.DiffFragmentStateAdapter
import com.ubadahj.qidianundergroud.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReaderContainerFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: ReaderContainerLayoutBinding? = null
    private val adapter by lazy { ChapterReaderAdapter(this) }

    @Inject
    lateinit var groupRepo: GroupRepository

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
            var first = true
            viewModel.getChapters(viewModel.selectedBook.value!!).collect {
                when (it) {
                    Resource.Loading -> {}
                    is Resource.Error -> findNavController().popBackStack()
                    is Resource.Success -> {
                        adapter.submitList(it.data.sortedBy(Group::firstChapter))
                        if (first) {
                            first = false
                            viewModel.selectedGroup.value?.let { group ->
                                binding?.pager?.setCurrentItem(
                                    adapter.currentList.indexOf(group),
                                    false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private class ChapterReaderAdapter(
        fragment: Fragment
    ) : DiffFragmentStateAdapter<Group>(fragment, GroupDiffCallback) {
        override fun createFragment(position: Int): Fragment {
            return ChapterFragment.newInstance(currentList[position].link)
        }
    }

}