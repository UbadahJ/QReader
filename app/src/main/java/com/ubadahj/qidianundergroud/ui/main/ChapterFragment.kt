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
import com.ubadahj.qidianundergroud.databinding.ChapterFragmentBinding
import com.ubadahj.qidianundergroud.models.Content
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.preferences.ReaderPreferences
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.ui.adapters.ContentAdapter
import com.ubadahj.qidianundergroud.ui.adapters.decorations.StickyHeaderManager
import com.ubadahj.qidianundergroud.ui.adapters.factories.ChapterViewHolderFactory
import com.ubadahj.qidianundergroud.ui.dialog.ContentPreferencesDialog
import com.ubadahj.qidianundergroud.ui.listeners.OnGestureListener
import com.ubadahj.qidianundergroud.ui.models.ContentHeaderConfig
import com.ubadahj.qidianundergroud.ui.models.ContentUIItem
import com.ubadahj.qidianundergroud.utils.ui.addOnScrollStateListener
import com.ubadahj.qidianundergroud.utils.ui.linearScroll
import com.ubadahj.qidianundergroud.utils.ui.preserveState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class ChapterFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: ChapterFragmentBinding? = null
    private var menu = ContentPreferencesDialog()
    private val headerConfig = ContentHeaderConfig(
        onBackPressed = { requireActivity().onBackPressed() },
        onMenuPressed = { menu.show(requireActivity().supportFragmentManager, null) }
    )
    private var baseAdapter = ContentAdapter(headerConfig = headerConfig) {
        stickyManager?.toggleVisibility()
    }
    private var stickyManager: StickyHeaderManager<ContentUIItem>? = null

    @Inject
    lateinit var groupRepo: GroupRepository

    @Inject
    lateinit var preferences: ReaderPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ChapterFragmentBinding.inflate(inflater, container, false).apply {
            binding = this
            lifecycleScope.launch {
                launch {
                    viewModel.selectedGroup.flowWithLifecycle(lifecycle).collect { group ->
                        group?.apply { init(this) }
                    }
                }
                launch {
                    viewModel.selectedContent.flowWithLifecycle(lifecycle).collect { content ->
                        content?.apply {
                            viewModel.selectedGroup.value?.run {
                                groupRepo.updateLastRead(this, getIndex())
                            }
                        }
                    }
                }
            }
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding?.apply {
            chapterRecyclerView.adapter = baseAdapter
            chapterRecyclerView.addOnScrollStateListener { rc, state ->
                if (state != RecyclerView.SCROLL_STATE_IDLE)
                    return@addOnScrollStateListener

                val firstPos =
                    (rc.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                if (firstPos != RecyclerView.NO_POSITION)
                    viewModel.setSelectedContent(baseAdapter.currentList.map { it.content }[firstPos])
            }
            stickyManager = StickyHeaderManager(
                chapterRecyclerView,
                baseAdapter,
                ChapterViewHolderFactory.header(chapterStickyGroup, headerConfig)
            )
            configureSwipeGestures()
            configurePreferencesFlow()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init(chapters: Group) {
        binding?.apply {
            errorGroup.errorButton.setOnClickListener { getChapterContents(chapters, true) }
        }
        getChapterContents(chapters)
    }

    private fun ChapterFragmentBinding.configurePreferencesFlow() =
        lifecycleScope.launch {
            preferences.fontScale.asFlow().flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .collect {
                    baseAdapter.scaleFactor = { it.toFloat() / 10 }
                    chapterRecyclerView.preserveState {
                        adapter = baseAdapter
                    }
                }
        }

    private fun getChapterContents(group: Group, refresh: Boolean = false) {
        lifecycleScope.launch {
            viewModel
                .getChapterContents(group, refresh)
                .flowWithLifecycle(lifecycle)
                .collect {
                    updateRecyclerAdapter(it)
                    binding?.updateUIIndicators(it)
                }
        }
    }

    private fun ChapterFragmentBinding.updateUIIndicators(resource: Resource<List<Content>>) {
        when (resource) {
            is Resource.Error -> {
                progressBar.visibility = View.GONE
                chapterRecyclerView.visibility = View.GONE
                errorGroup.root.visibility = View.VISIBLE
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
                if (!preferences.lockFontScale.get())
                    preferences.fontScale.set((scale * 10).roundToInt())
            }

            private fun selectChapterGroup(
                predicate: (current: Group, other: Group) -> Boolean
            ) {
                val group = viewModel.selectedGroup.value
                viewModel.selectedBook.value?.let { book ->
                    lifecycleScope.launch {
                        groupRepo.getGroups(book).first()
                            .firstOrNull { other -> group?.let { predicate(it, other) } == true }
                            ?.let { viewModel.setSelectedGroup(it) }
                    }
                }
            }
        })
    }

    private fun updateRecyclerAdapter(resource: Resource<List<Content>>) {
        val group = viewModel.selectedGroup.value
        val chapter = viewModel.selectedContent.value
        val hasDataChanged = chapter == null ||
                baseAdapter.currentList.isEmpty() ||
                chapter.groupLink != group?.link

        if (hasDataChanged && resource is Resource.Success) {
            baseAdapter.submitList(resource.data.toUIModel())
            viewModel.selectedGroup.value?.let { group ->
                val index = (if (group.lastRead != 0) group.lastRead - group.firstChapter else 0)
                    .toInt()

                viewModel.setSelectedContent(resource.data[index])
                binding?.chapterRecyclerView?.linearScroll(index * 2)
            }
        }
    }

    private fun Content.getIndex(): Int {
        return try {
            title.split(':').first().trim().split(" ").last().toInt()
        } catch (e: NoSuchElementException) {
            viewModel.selectedGroup.value?.lastChapter?.toInt() ?: throw IllegalStateException(
                "Failed to get lastChapter from ViewModel selectChapterGroup"
            )
        }
    }

    private fun List<Content>.toUIModel(): List<ContentUIItem> = flatMap {
        listOf(
            ContentUIItem.ContentUITitleItem(it), ContentUIItem.ContentUIContentItem(it)
        )
    }
}
