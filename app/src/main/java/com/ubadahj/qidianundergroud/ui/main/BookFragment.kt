package com.ubadahj.qidianundergroud.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.services.DownloadService
import com.ubadahj.qidianundergroud.ui.adapters.GroupAdapter
import com.ubadahj.qidianundergroud.ui.dialog.GroupDetailsDialog
import com.ubadahj.qidianundergroud.utils.repositories.addToLibrary

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
        viewModel.selectedBook.observe(viewLifecycleOwner, { value ->
            value?.apply { init(this) }
        })
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

            chapterListView.adapter = GroupAdapter(listOf(), {
                viewModel.selectedGroup.value = it
                findNavController().navigate(
                    BookFragmentDirections.actionBookFragmentToChapterFragment()
                )
            }) {
                GroupDetailsDialog(it)
                    .show(requireActivity().supportFragmentManager, null)
            }

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

        loadGroups(book)
    }

    private fun loadGroups(book: Book) {
        viewModel.getChapters(requireContext(), book).observe(viewLifecycleOwner) { resource ->
            binding?.apply {
                when (resource) {
                    is Resource.Success -> {
                        materialCardView.visibility = View.VISIBLE
                        loadingProgress.visibility = View.GONE
                        (chapterListView.adapter as? GroupAdapter)
                            ?.submitList(resource.data!!)
                    }
                    is Resource.Loading -> {
                        loadingProgress.visibility = View.VISIBLE
                        materialCardView.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        loadingProgress.visibility = View.GONE
                        materialCardView.visibility = View.GONE
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

}