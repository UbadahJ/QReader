package com.ubadahj.qidianundergroud.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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
import com.ubadahj.qidianundergroud.ui.adapters.ContentAdapterPreferences
import com.ubadahj.qidianundergroud.ui.adapters.decorations.StickyHeaderManager
import com.ubadahj.qidianundergroud.ui.adapters.factories.ChapterViewHolderFactory
import com.ubadahj.qidianundergroud.ui.dialog.ContentPreferencesDialog
import com.ubadahj.qidianundergroud.ui.models.ContentHeaderConfig
import com.ubadahj.qidianundergroud.ui.models.ContentUIItem
import com.ubadahj.qidianundergroud.ui.viewmodels.ChapterViewModel
import com.ubadahj.qidianundergroud.utils.collectNotNull
import com.ubadahj.qidianundergroud.utils.ui.addOnScrollStateListener
import com.ubadahj.qidianundergroud.utils.ui.linearScroll
import com.ubadahj.qidianundergroud.utils.ui.preserveState
import com.ubadahj.qidianundergroud.utils.ui.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

const val CHAPTER_FRAGMENT_ARG_GROUP_LINK = "groupLink"


@AndroidEntryPoint
class ChapterFragment : Fragment() {

    private val viewModel: ChapterViewModel by viewModels()
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

    private val groupLink by lazy {
        requireArguments().getString(CHAPTER_FRAGMENT_ARG_GROUP_LINK)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ChapterFragmentBinding.inflate(inflater, container, false).apply {
            binding = this
            viewModel.init(groupLink)
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
            configurePreferencesFlow()
        }

        lifecycleScope.launch {
            launch {
                viewModel.group.flowWithLifecycle(lifecycle).collectNotNull {
                    init(it)
                }
            }
            launch {
                viewModel.selectedContent.flowWithLifecycle(lifecycle).collectNotNull {
                    viewModel.updateLastRead(it)
                }
            }
            launch {
                viewModel.contents.collect {
                    updateRecyclerAdapter(it)
                    binding?.updateUIIndicators(it)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init(chapters: Group) {
        binding?.apply {
            errorGroup.errorButton.setOnClickListener { viewModel.getContents() }
        }
    }

    private fun ChapterFragmentBinding.configurePreferencesFlow() = lifecycleScope.launch {
        launch {
            preferences.fontScale.asFlow()
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .map { it.toFloat() / 10 }
                .collect {
                    updateAdapterPreferences { copy(scaleFactor = it) }
                }
        }
        launch {
            preferences.lineSpacingMultiplier.asFlow()
                .flowWithLifecycle(lifecycle, Lifecycle.State.RESUMED)
                .map { it.toFloat() / 10 }
                .collect {
                    updateAdapterPreferences { copy(lineSpacing = it) }
                }
        }
    }

    private fun ChapterFragmentBinding.updateUIIndicators(resource: Resource<List<Content>>) {
        when (resource) {
            is Resource.Error -> {
                progressBar.visible = false
                chapterRecyclerView.visible = false
                errorGroup.root.visible = true
            }
            Resource.Loading -> {
                chapterRecyclerView.visible = false
                errorGroup.root.visible = false
                progressBar.visible = true
            }
            is Resource.Success -> {
                progressBar.visible = false
                errorGroup.root.visible = false
                chapterRecyclerView.visible = true
            }
        }
    }

    private fun updateRecyclerAdapter(resource: Resource<List<Content>>) {
        val group = viewModel.group.value
        val chapter = viewModel.selectedContent.value
        val hasDataChanged = chapter == null ||
                baseAdapter.currentList.isEmpty() ||
                chapter.groupLink != group?.link

        if (hasDataChanged && resource is Resource.Success) {
            val models = resource.data.toUIModel()
            baseAdapter.submitList(models)
            group?.let {
                val index = (if (group.lastRead != 0) group.lastRead - group.firstChapter else 0)
                    .toInt()

                viewModel.setSelectedContent(resource.data.run { getOrNull(index) ?: first() })
                binding?.chapterRecyclerView?.linearScroll(
                    models.mapIndexed { i, it -> i to it }
                        .filter { it.second is ContentUIItem.ContentUIContentItem }
                        .firstOrNull { it.second.content == viewModel.selectedContent.value }
                        ?.first
                        ?.minus(1) ?: 0
                )
            }
        }
    }

    private fun updateAdapterPreferences(
        action: ContentAdapterPreferences.() -> ContentAdapterPreferences
    ) {
        baseAdapter.preferences = baseAdapter.preferences.run(action)
        binding?.chapterRecyclerView?.preserveState {
            adapter = baseAdapter
        }
    }

    private fun List<Content>.toUIModel(): List<ContentUIItem> = flatMap { content ->
        mutableListOf<ContentUIItem>(
            ContentUIItem.ContentUITitleItem(content)
        ).apply {
            addAll(content.contents.lines().map {
                ContentUIItem.ContentUIContentItem(content, it)
            })
        }
    }

    companion object {
        fun newInstance(link: String) = ChapterFragment().apply {
            arguments = bundleOf(CHAPTER_FRAGMENT_ARG_GROUP_LINK to link)
        }
    }
}
