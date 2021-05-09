package com.ubadahj.qidianundergroud.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Metadata
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.services.DownloadService
import com.ubadahj.qidianundergroud.ui.adapters.GroupAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.dialog.GroupDetailsDialog
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.models.setNotifications
import com.ubadahj.qidianundergroud.utils.repositories.addToLibrary
import com.ubadahj.qidianundergroud.utils.repositories.removeFromLibrary
import com.ubadahj.qidianundergroud.utils.ui.visible
import kotlinx.coroutines.flow.collect

class BookFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: BookFragmentBinding? = null
    private var menuAdapter = MenuAdapter(listOf())

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
            lifecycleScope.launchWhenResumed {
                value?.apply { init(this) }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.selectedBook.value == null)
            findNavController().popBackStack()
    }

    private fun init(book: Book) {
        configureMenu(book)
        binding?.apply {
            bookTitle.text = book.name

            bookImage.load(R.drawable.placeholder_600_800)
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
                lifecycleScope.launchWhenResumed {
                    if (book.inLibrary) {
                        book.removeFromLibrary(requireContext())
                        Snackbar.make(root, "Removed book to the library", Snackbar.LENGTH_SHORT)
                            .show()
                    } else {
                        book.addToLibrary(requireContext())
                        Snackbar.make(root, "Added book to the library", Snackbar.LENGTH_SHORT)
                            .show()
                    }

                    BookRepository(requireContext())
                        .getBookById(book.id)
                        .collect { viewModel.selectedBook.postValue(it) }
                }
            }

            menuImageView.setOnClickListener {
                MenuDialog(menuAdapter).show(requireActivity().supportFragmentManager, null)
            }

            if (book.inLibrary) {
                libraryButton.load(R.drawable.heart_filled)
                libraryLabel.text = "In library"
            } else {
                libraryButton.load(R.drawable.heart)
                libraryLabel.text = "Add to library"
            }

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

            viewModel.getMetadata(requireContext(), book).observe(viewLifecycleOwner) {
                it.data?.apply {
                    bookImage.load(coverPath)
                    bookAuthor.text = author
                    bookDesc.text = description
                    bookGenre.text = category
                    bookGenre.visible = true

                    configureMenu(book, this)
                }
            }
        }

        loadGroups(book)
    }

    private fun configureMenu(book: Book, metadata: Metadata? = null) {
        val menuItems: MutableList<MenuDialogItem> = mutableListOf(
            MenuDialogItem("Mark all chapters as read", R.drawable.check) {
                lifecycleScope.launchWhenResumed {
                    BookRepository(requireContext()).markAllRead(book)
                }
            }
        )

        if (metadata != null) {
            val notifyOp = if (metadata.enableNotification) "Disable" else "Enable"
            menuItems.add(
                MenuDialogItem("$notifyOp notifications for this book", R.drawable.bell) {
                    lifecycleScope.launchWhenResumed {
                        book.setNotifications(requireContext(), !metadata.enableNotification)
                    }
                }
            )
        }

        menuAdapter.submitList(menuItems)
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