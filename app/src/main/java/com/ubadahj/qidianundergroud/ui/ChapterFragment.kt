package com.ubadahj.qidianundergroud.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.mikepenz.fastadapter.FastAdapter
import com.ubadahj.qidianundergroud.databinding.ChapterFragmentBinding
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.ui.adapters.ChapterContentAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.ChapterContentItem
import com.ubadahj.qidianundergroud.utils.getHtml
import kotlinx.coroutines.delay
import org.jsoup.Jsoup

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
            progressBar.visibility = View.VISIBLE
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(chapters.link)

            chapterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            lifecycleScope.launchWhenStarted {
                val items = webView.getChapterContents()
                chapterRecyclerView.adapter = getAdapter(items)
                toolbar.appbar.title = items[0].chapterName
                progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun WebView.getChapterContents(): List<ChapterContentItem> {
        var doc = Jsoup.parse(getHtml())!!
        while ("Chapter" !in doc.text()) {
            doc = Jsoup.parse(getHtml())!!
            d { "getChapterContents: delaying" }
            delay(300)
        }
        doc.select("br").forEach { it.remove() }
        return doc.select(".well")
            .filter { "Chapter" in it.text() }
            .filter { it.select("h2.text-center").first() != null }
            .map {
                ChapterContentItem(
                    fromHtml(it.select("h2.text-center").first().html()),
                    fromHtml(it.select("p").outerHtml())
                )
            }
    }

    private fun getAdapter(items: List<ChapterContentItem>) =
        FastAdapter.with(ChapterContentAdapter(items)).apply {
            onTouchListener = { _, _, _, item, _ ->
                binding?.toolbar?.appbar?.title = item.chapterName
                true
            }
        }

    private fun fromHtml(string: String) =
        HtmlCompat.fromHtml(string, HtmlCompat.FROM_HTML_MODE_LEGACY)
}