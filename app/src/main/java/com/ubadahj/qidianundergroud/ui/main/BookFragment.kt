package com.ubadahj.qidianundergroud.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository
import com.ubadahj.qidianundergroud.services.DownloadService
import com.ubadahj.qidianundergroud.ui.adapters.ChapterAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.ChapterItem
import com.ubadahj.qidianundergroud.utils.models.lastChapter
import com.ubadahj.qidianundergroud.utils.repositories.addToLibrary
import com.ubadahj.qidianundergroud.utils.repositories.updateLastRead
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BookFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: BookFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BookFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            lastUpdated.text = if (book.completed) "Completed" else book.lastUpdated
            chapterListView.layoutManager = GridLayoutManager(requireContext(), 2)
            libraryButton.setOnClickListener {
                book.addToLibrary(requireContext())
                Snackbar.make(root, "Added book to the library", Snackbar.LENGTH_SHORT).show()
                libraryButton.visibility = View.GONE
            }
            if (book.inLibrary)
                libraryButton.visibility = View.GONE

            downloadImageView.setOnClickListener {
                val work = OneTimeWorkRequestBuilder<DownloadService>().apply {
                    setInputData(Data.Builder().apply {
                        putString("book_id", book.id)
                    }.build())
                }.build()
                WorkManager.getInstance(requireContext()).enqueueUniqueWork(
                    "download-service", ExistingWorkPolicy.KEEP, work
                )
            }
        }

        viewModel.getChapters(requireContext(), book).observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding?.loadingProgress?.visibility = View.GONE
                    binding?.chapterListView?.adapter = createAdapter(book, resource.data!!)
                }
                is Resource.Loading -> binding?.loadingProgress?.visibility = View.VISIBLE
                is Resource.Error -> {
                    binding?.loadingProgress?.visibility = View.GONE
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

    private fun createAdapter(book: Book, groups: List<ChapterGroup>): FastAdapter<ChapterItem> =
        FastAdapter.with(ChapterAdapter(book, groups)).apply {
            onClickListener = { _, _, item, _ ->
                book.updateLastRead(requireContext(), item.chapter.lastChapter)
                viewModel.selectedChapter.value = item.chapter
                lifecycleScope.launch {
                    viewModel.selectedBook.value =
                        ChapterGroupRepository(this@BookFragment.requireContext())
                            .getBook(groups.first())
                            .first()
                }
                findNavController().navigate(
                    BookFragmentDirections.actionBookFragmentToChapterFragment()
                )
                true
            }
        }

}