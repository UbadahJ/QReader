package com.ubadahj.qidianundergroud.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.database.Database
import com.ubadahj.qidianundergroud.database.DatabaseInstance
import com.ubadahj.qidianundergroud.databinding.BookFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.ui.adapters.ChapterAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.ChapterItem

class BookFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val api = Api(true)

    private var binding: BookFragmentBinding? = null
    private lateinit var database: Database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BookFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = DatabaseInstance.getInstance(requireContext())
        viewModel.selectedBook.observe(viewLifecycleOwner) {
            it?.apply { init(this) }
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.selectedBook.value == null)
            findNavController().popBackStack()
    }

    private fun init(book: Book) {
        binding?.apply {
            header.text = book.name
            lastUpdated.text = if (book.status) "Completed" else book.formattedLastUpdated
            chapterListView.layoutManager = GridLayoutManager(requireContext(), 2)
            libraryButton.setOnClickListener {
                database.add(book)
                Snackbar.make(root, "Added book to the library", Snackbar.LENGTH_SHORT).show()
                libraryButton.visibility = View.GONE
            }
            if (database.get().contains(book))
                libraryButton.visibility = View.GONE
        }

        viewModel.getChapters(book).observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    book.chapterGroups = resource.data!!
                    binding?.chapterListView?.adapter = createAdapter(book)
                }
                is Resource.Loading -> {
                }
                is Resource.Error -> {
                    binding?.apply {
                        Snackbar.make(root, R.string.error_refreshing, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun createAdapter(book: Book): FastAdapter<ChapterItem> =
        FastAdapter.with(ChapterAdapter(book)).apply {
            onClickListener = { _, _, item, _ ->
                book.lastRead = item.chapter.lastChapter
                database.save()
                CustomTabsIntent.Builder()
                    .build()
                    .launchUrl(requireContext(), item.chapter.link.toUri())
                notifyAdapterDataSetChanged()
                true
            }
        }

}