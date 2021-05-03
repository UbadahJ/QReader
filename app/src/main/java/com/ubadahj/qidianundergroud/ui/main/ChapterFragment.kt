package com.ubadahj.qidianundergroud.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.ChapterFragmentBinding
import com.ubadahj.qidianundergroud.models.Chapter
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.ui.adapters.ChapterAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.models.firstChapter
import com.ubadahj.qidianundergroud.utils.models.lastChapter
import com.ubadahj.qidianundergroud.utils.repositories.updateLastRead
import com.ubadahj.qidianundergroud.utils.ui.addOnScrollStateListener
import com.ubadahj.qidianundergroud.utils.ui.linearScroll

class ChapterFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: ChapterFragmentBinding? = null
    private var menu: MenuDialog = MenuDialog(
        MenuAdapter().apply {
            submitList(listOf(MenuDialogItem("Loading", R.drawable.pulse)))
        }
    )
    private var adapter: ChapterAdapter = ChapterAdapter(listOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ChapterFragmentBinding.inflate(inflater, container, false).apply {
            binding = this
            viewModel.selectedGroup.observe(viewLifecycleOwner, { group ->
                group?.apply { init(this) }
            })
            viewModel.selectedChapter.observe(viewLifecycleOwner, { chapter ->
                chapter?.apply {
                    toolbar.appbar.title = this.title
                    viewModel.selectedGroup.value?.updateLastRead(requireContext(), getIndex())
                }
            })
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding?.apply {
            (requireActivity() as AppCompatActivity).apply {
                setSupportActionBar(toolbar.appbar)
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }

            chapterRecyclerView.adapter = adapter
            chapterRecyclerView.addOnScrollStateListener { rc, state ->
                if (state != RecyclerView.SCROLL_STATE_IDLE)
                    return@addOnScrollStateListener

                viewModel.selectedChapter.value = this@ChapterFragment.adapter.currentList[
                        (rc.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                ]
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu -> {
                menu.show(requireActivity().supportFragmentManager, null)
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.simple_menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init(chapters: ChapterGroup) {
        binding?.apply {
            errorButton.setOnClickListener { getChapterContents(chapters, true) }
        }
        getChapterContents(chapters)
    }

    private fun getChapterContents(group: ChapterGroup, refresh: Boolean = false) {
        viewModel
            .getChapterContents(requireContext(), group, refresh)
            .observe(viewLifecycleOwner, {
                binding?.updateUIIndicators(it)
                updateRecyclerAdapter(it)
            })
    }

    private fun ChapterFragmentBinding.updateUIIndicators(resource: Resource<List<Chapter>>) {
        when (resource) {
            is Resource.Error -> {
                toolbar.appbar.title = "Error"
                errorGroup.visibility = View.VISIBLE
                progressBar.visibility = View.GONE

                menu.adapter.submitList(
                    listOf(MenuDialogItem("Error", R.drawable.unlink))
                )
            }
            is Resource.Loading -> {
                toolbar.appbar.title = "Loading"
                progressBar.visibility = View.VISIBLE
                errorGroup.visibility = View.GONE
            }
            is Resource.Success -> {
                progressBar.visibility = View.GONE
                errorGroup.visibility = View.GONE
            }
        }
    }

    private fun updateRecyclerAdapter(resource: Resource<List<Chapter>>) {
        val group = viewModel.selectedGroup.value
        val chapter = viewModel.selectedChapter.value
        val hasDataChanged = chapter == null
                || adapter.currentList.isEmpty()
                || chapter.groupLink != group?.link

        if (hasDataChanged && resource is Resource.Success) {
            adapter.submitList(resource.data!!)
            updateMenu(resource.data)

            viewModel.selectedGroup.value?.let { group ->
                val index = if (group.lastRead != 0) group.lastRead - group.firstChapter else 0

                viewModel.selectedChapter.value = resource.data[index]
                binding?.chapterRecyclerView?.linearScroll(index)
            }
        }
    }

    private fun updateMenu(items: List<Chapter>) {
        menu.adapter.submitList(items.map { MenuDialogItem(it.title) })
        menu.onClick = { _, pos, _ ->
            binding?.apply {
                viewModel.selectedChapter.value = adapter.currentList[pos]
                chapterRecyclerView.linearScroll(pos)
            }
            menu.dismiss()
        }
    }

    private fun Chapter.getIndex(): Int {
        return try {
            title.split(':').first().trim().split(" ").last().toInt()
        } catch (e: NoSuchElementException) {
            viewModel.selectedGroup.value?.lastChapter ?: throw IllegalStateException(
                "Failed to get lastChapter from ViewModel selectChapterGroup"
            )
        }
    }
}