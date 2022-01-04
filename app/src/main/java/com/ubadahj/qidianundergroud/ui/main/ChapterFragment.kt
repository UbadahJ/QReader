package com.ubadahj.qidianundergroud.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.ChapterFragmentBinding
import com.ubadahj.qidianundergroud.models.Chapter
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.preferences.ReaderPreferences
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository
import com.ubadahj.qidianundergroud.ui.adapters.ChapterAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.adapters.decorations.HeaderItemDecoration
import com.ubadahj.qidianundergroud.ui.adapters.factories.ChapterViewHolderType
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.listeners.OnGestureListener
import com.ubadahj.qidianundergroud.ui.models.ChapterUIItem
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.models.firstChapter
import com.ubadahj.qidianundergroud.utils.models.lastChapter
import com.ubadahj.qidianundergroud.utils.ui.addOnScrollStateListener
import com.ubadahj.qidianundergroud.utils.ui.linearScroll
import com.ubadahj.qidianundergroud.utils.ui.preserveState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class ChapterFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: ChapterFragmentBinding? = null
    private var menu: MenuDialog = MenuDialog(
        MenuAdapter(listOf(MenuDialogItem("Loading", R.drawable.pulse)))
    )
    private var adapter: ChapterAdapter = ChapterAdapter(listOf()) { 1f }

    @Inject
    lateinit var groupRepo: ChapterGroupRepository

    @Inject
    lateinit var preferences: ReaderPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ChapterFragmentBinding.inflate(inflater, container, false).apply {
            binding = this
            viewModel.selectedGroup.observe(viewLifecycleOwner) { group ->
                group?.apply { init(this) }
            }
            viewModel.selectedChapter.observe(viewLifecycleOwner) { chapter ->
                chapter?.apply {
                    viewModel.selectedGroup.value?.run {
                        groupRepo.updateLastRead(this, getIndex())
                    }
                }
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding?.apply {
            chapterRecyclerView.adapter = adapter
            chapterRecyclerView.addOnScrollStateListener { rc, state ->
                if (state != RecyclerView.SCROLL_STATE_IDLE)
                    return@addOnScrollStateListener

                val firstPos =
                    (rc.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                viewModel.selectedChapter.value = this@ChapterFragment.adapter.currentList
                    .map { it.chapter }[firstPos]
            }
            chapterRecyclerView.addItemDecoration(HeaderItemDecoration(chapterRecyclerView) {
                adapter.getItemViewType(it) == ChapterViewHolderType.TITLE.ordinal
            })
            configureSwipeGestures()
            configurePreferencesFlow()
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
            errorGroup.errorButton.setOnClickListener { getChapterContents(chapters, true) }
        }
        getChapterContents(chapters)
    }

    private fun ChapterFragmentBinding.configurePreferencesFlow() =
        lifecycleScope.launchWhenResumed {
            preferences.fontScale.asFlow().flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect {
                    adapter.scaleFactor = { it }
                    chapterRecyclerView.preserveState {
                        adapter = this@ChapterFragment.adapter
                    }
                }
        }

    private fun getChapterContents(group: ChapterGroup, refresh: Boolean = false) {
        viewModel
            .getChapterContents(group, refresh)
            .observe(
                viewLifecycleOwner,
                {
                    updateRecyclerAdapter(it)
                    binding?.updateUIIndicators(it)
                }
            )
    }

    private fun ChapterFragmentBinding.updateUIIndicators(resource: Resource<List<Chapter>>) {
        when (resource) {
            is Resource.Error -> {
                progressBar.visibility = View.GONE
                chapterRecyclerView.visibility = View.GONE
                errorGroup.root.visibility = View.VISIBLE

                menu.adapter.submitList(
                    listOf(MenuDialogItem("Error", R.drawable.unlink))
                )
            }
            Resource.Loading -> {
                chapterRecyclerView.visibility = View.GONE
                errorGroup.root.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            }
            is Resource.Success -> {
                progressBar.visibility = View.GONE
                errorGroup.root.visibility = View.GONE
                chapterRecyclerView.visibility = View.VISIBLE
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun ChapterFragmentBinding.configureSwipeGestures() {
        chapterRecyclerView.setOnTouchListener(object : OnGestureListener(requireContext()) {
            override fun onSwipeLeft() {
                selectChapterGroup { current, other ->
                    current.lastChapter == other.firstChapter - 1
                }
            }

            override fun onSwipeRight() {
                selectChapterGroup { current, other ->
                    current.firstChapter == other.lastChapter + 1
                }
            }

            override fun onScaleView(scale: Float) {
                preferences.fontScale.set(scale)
            }

            private fun selectChapterGroup(
                predicate: (current: ChapterGroup, other: ChapterGroup) -> Boolean
            ) {
                val group = viewModel.selectedGroup.value
                viewModel.selectedBook.value?.let { book ->
                    lifecycleScope.launchWhenCreated {
                        groupRepo.getGroups(book).first()
                            .firstOrNull { other -> group?.let { predicate(it, other) } == true }
                            ?.let { viewModel.selectedGroup.postValue(it) }
                    }
                }
            }
        })
    }

    private fun updateRecyclerAdapter(resource: Resource<List<Chapter>>) {
        val group = viewModel.selectedGroup.value
        val chapter = viewModel.selectedChapter.value
        val hasDataChanged = chapter == null ||
                adapter.currentList.isEmpty() ||
                chapter.groupLink != group?.link

        if (hasDataChanged && resource is Resource.Success) {
            adapter.submitList(resource.data.toUIModel())
            updateMenu(resource.data)

            viewModel.selectedGroup.value?.let { group ->
                val index = if (group.lastRead != 0) group.lastRead - group.firstChapter else 0

                viewModel.selectedChapter.value = resource.data[index]
                binding?.chapterRecyclerView?.linearScroll(index * 2)
            }
        }
    }

    private fun updateMenu(items: List<Chapter>) {
        menu.adapter.submitList(
            items.mapIndexed { i, it ->
                MenuDialogItem(it.title) {
                    binding?.apply {
                        viewModel.selectedChapter.value = it
                        chapterRecyclerView.linearScroll(i)
                    }
                }
            }
        )
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

    private fun List<Chapter>.toUIModel(): List<ChapterUIItem> = flatMap {
        listOf(
            ChapterUIItem.ChapterUITitleItem(it), ChapterUIItem.ChapterUIContentItem(it)
        )
    }
}
