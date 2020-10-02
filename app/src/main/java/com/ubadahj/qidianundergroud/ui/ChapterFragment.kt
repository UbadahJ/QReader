package com.ubadahj.qidianundergroud.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.ChapterFragmentBinding
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.ui.adapters.ChapterContentAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.ChapterContentItem

class ChapterFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: ChapterFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ChapterFragmentBinding.inflate(inflater, container, false)
        viewModel.selectedChapter.observe(viewLifecycleOwner, {
            it?.apply { init(this) }
        })
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun init(chapters: ChapterGroup) {
        binding?.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar.appbar)
            toolbar.appbar.title = "Loading"
            chapterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(chapters.link)
            webView.getChapterContents(chapters).observe(viewLifecycleOwner, {
                when (it) {
                    is Resource.Success -> {
                        chapterRecyclerView.adapter = getAdapter(it.data!!)
                        toolbar.appbar.title = it.data[0].chapterName
                        progressBar.visibility = View.GONE
                    }
                    is Resource.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is Resource.Error -> {
                        toolbar.appbar.title = "Error"
                        Snackbar.make(root, R.string.time_out, Snackbar.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                }
            })
        }
    }

    private fun getAdapter(items: List<ChapterContentItem>) =
        FastAdapter.with(ChapterContentAdapter(items)).apply {
            onTouchListener = { _, _, _, item, _ ->
                binding?.toolbar?.appbar?.title = item.chapterName
                true
            }
        }

    private fun WebView.getChapterContents(chapters: ChapterGroup, refresh: Boolean = false) =
        viewModel.getChapterContents(
            this,
            viewModel.selectedBook.value!!,
            chapters,
            refresh
        )
}