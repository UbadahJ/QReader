package com.ubadahj.qidianundergroud.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ubadahj.qidianundergroud.databinding.BookChaptersFragmentBinding
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.ui.adapters.GroupAdapter
import com.ubadahj.qidianundergroud.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookChaptersFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: BookChaptersFragmentBinding? = null
    private var groupJob: Job? = null

    private val adapter: GroupAdapter = GroupAdapter(onClick = {
        findNavController().navigate(
            BookChaptersFragmentDirections.actionBookChaptersFragmentToChapterFragment(it.link)
        )
    }) { GroupDetailsDialog(it).show(requireActivity().supportFragmentManager, null) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupJob?.cancel()
        val book = viewModel.selectedBook.value
        if (book == null) {
            requireActivity().onBackPressed()
            return
        }

        groupJob = lifecycleScope.launch {
            viewModel.getChapters(book).collect {
                binding?.configureGroups(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return BookChaptersFragmentBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            collapsingLayout.title = "Table of Contents"
            chapterListView.adapter = adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun BookChaptersFragmentBinding.configureGroups(resource: Resource<List<Group>>) {
        when (resource) {
            Resource.Loading -> {}
            is Resource.Success -> {
                adapter.submitList(resource.data)
            }
            is Resource.Error -> {}
        }
    }

}
