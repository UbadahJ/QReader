package com.ubadahj.qidianundergroud.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.github.ajalt.timberkt.d
import com.ubadahj.qidianundergroud.databinding.ChapterFragmentBinding
import com.ubadahj.qidianundergroud.models.ChapterGroup
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
            progressBar.visibility = View.VISIBLE
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(chapters.link)
            lifecycleScope.launchWhenStarted {
                val builder = SpannableStringBuilder()
                webView.getChapterContents().forEach {
                    builder.append(it)
                }
                chapterText.text = builder
                progressBar.visibility = View.GONE
            }
        }
    }

    private suspend fun WebView.getChapterContents(): List<Spanned> {
        var doc = Jsoup.parse(getHtml())!!
        while ("Chapter" !in doc.text()) {
            doc = Jsoup.parse(getHtml())!!
            d { "getChapterContents: delaying" }
            delay(300)
        }
        val elements = doc.select(".well")
            .filter { "Chapter" in it.text() }
            .map { HtmlCompat.fromHtml(it.html(), HtmlCompat.FROM_HTML_MODE_LEGACY) }
        return elements.slice(1 until elements.size)
    }

}