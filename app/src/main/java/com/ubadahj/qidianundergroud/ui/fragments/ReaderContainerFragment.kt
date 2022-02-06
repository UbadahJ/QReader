package com.ubadahj.qidianundergroud.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ubadahj.qidianundergroud.databinding.ReaderContainerLayoutBinding
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReaderContainerFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: ReaderContainerLayoutBinding? = null

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
        lifecycleScope.launch {
            val list = groupRepo.getGroups(viewModel.selectedBook.value!!).first()
            binding?.pager?.apply {
                adapter = ChapterReaderAdapter(this@ReaderContainerFragment, list)
                viewModel.selectedGroup.value?.let { group ->
                    currentItem = list.indexOf(group)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private class ChapterReaderAdapter(
        fragment: Fragment,
        val groups: List<Group>
    ) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = groups.size

        override fun createFragment(position: Int): Fragment {
            return ChapterFragment.newInstance(groups[position].link)
        }
    }

}