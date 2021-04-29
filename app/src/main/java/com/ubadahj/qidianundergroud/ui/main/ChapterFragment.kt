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
import com.ubadahj.qidianundergroud.ui.adapters.ChapterContentAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.models.lastChapter
import com.ubadahj.qidianundergroud.utils.repositories.updateLastRead
import com.ubadahj.qidianundergroud.utils.ui.addOnScrollStateListener

class ChapterFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: ChapterFragmentBinding? = null
    private var menu: MenuDialog = MenuDialog(
            MenuAdapter().apply {
                submitList(listOf(MenuDialogItem("Loading", R.drawable.pulse)))
            }
    )
    private var adapter: ChapterContentAdapter = ChapterContentAdapter(listOf())

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = ChapterFragmentBinding.inflate(inflater, container, false)
        viewModel.selectedChapter.observe(viewLifecycleOwner, { value ->
            value?.apply { init(this) }
        })
        return binding?.root
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

                val chapter = this@ChapterFragment.adapter.currentList[
                        (rc.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                ]
                toolbar.appbar.title = chapter.title
                viewModel.selectedChapter.value?.updateLastRead(
                    requireContext(),
                    chapter.getIndex()
                )
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

    private fun getChapterContents(chapters: ChapterGroup, refresh: Boolean = false) =
            viewModel.getChapterContents(requireContext(), chapters, refresh)
                    .observe(viewLifecycleOwner, {
                        binding?.apply {
                            when (it) {
                                is Resource.Success -> {
                                    adapter.submitList(it.data!!)
                                    updateMenu(it.data)
                                    toolbar.appbar.title = it.data.first().title
                                    progressBar.visibility = View.GONE
                                    errorGroup.visibility = View.GONE
                                }
                                is Resource.Loading -> {
                                    toolbar.appbar.title = "Loading"
                                    progressBar.visibility = View.VISIBLE
                                    errorGroup.visibility = View.GONE
                                }
                                is Resource.Error -> {
                                    toolbar.appbar.title = "Error"
                                    menu.adapter.submitList(
                                            listOf(MenuDialogItem("Error", R.drawable.unlink))
                                    )
                                    errorGroup.visibility = View.VISIBLE
                                    progressBar.visibility = View.GONE
                                }
                            }
                        }
                    })

    private fun updateMenu(items: List<Chapter>) {
        menu.adapter.submitList(items.map { MenuDialogItem(it.title) })
        menu.onClick = { _, pos, _ ->
            binding?.apply {
                chapterRecyclerView.layoutManager?.let {
                    (it as LinearLayoutManager).scrollToPositionWithOffset(pos, 0)
                }
            }
            menu.dismiss()
        }
    }

    private fun Chapter.getIndex(): Int {
        return try {
            title.split(':').first().trim().split(" ").last().toInt()
        } catch (e: NoSuchElementException) {
            viewModel.selectedChapter.value?.lastChapter ?: throw IllegalStateException(
                    "Failed to get lastChapter from ViewModel selectChapterGroup"
            )
        }
    }
}