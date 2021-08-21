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
import com.ubadahj.qidianundergroud.utils.models.markAsRead
import com.ubadahj.qidianundergroud.utils.models.setNotifications
import com.ubadahj.qidianundergroud.utils.repositories.addToLibrary
import com.ubadahj.qidianundergroud.utils.repositories.removeFromLibrary
import com.ubadahj.qidianundergroud.utils.ui.snackBar
import com.ubadahj.qidianundergroud.utils.ui.visible
import kotlinx.coroutines.flow.collect

class BookFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: BookFragmentBinding? = null
    private var menuAdapter = MenuAdapter(listOf())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BookFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedBook.observe(
            viewLifecycleOwner,
            { value ->
                lifecycleScope.launchWhenResumed {
                    value?.apply { init(this) }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.selectedBook.value == null)
            findNavController().popBackStack()
    }

    private fun init(book: Book, ignoreAvaliable: Boolean = false) {
        configureMenu(book)
        binding?.apply {
            bookTitle.text = book.name
            bookImage.load(R.drawable.placeholder_600_800)

            if (book.isAvailable || ignoreAvaliable) {
                configureLibraryButton(book)
                configureDownloadButton(book)
                configureMenuButton()

                configureGroupAdapter()
            } else {
                errorGroup.apply {
                    root.visible = true
                    errorText.text = "The book is not longer avaliable"
                    errorButton.text = "Continue"
                    errorButton.setOnClickListener {
                        init(book, true)
                    }
                }
            }

            lifecycleScope.launchWhenResumed {
                viewModel.getMetadata(requireContext(), book).observe(viewLifecycleOwner) {
                    configureMetadata(it, book)
                }
            }
        }

        loadGroups(book)
    }

    private fun BookFragmentBinding.configureLibraryButton(book: Book) {
        if (book.inLibrary) {
            libraryButton.load(R.drawable.heart_filled)
            libraryLabel.text = "In library"
        } else {
            libraryButton.load(R.drawable.heart)
            libraryLabel.text = "Add to library"
        }

        libraryButton.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                if (book.inLibrary) {
                    book.removeFromLibrary(requireContext())
                    root.snackBar("Removed book to the library")
                } else {
                    book.addToLibrary(requireContext())
                    root.snackBar("Added book to the library")
                }

                BookRepository(requireContext())
                    .getBookById(book.id)
                    .collect { viewModel.selectedBook.postValue(it) }
            }
        }
    }

    private fun BookFragmentBinding.configureDownloadButton(
        book: Book
    ) {
        downloadImageView.setOnClickListener {
            val work = OneTimeWorkRequestBuilder<DownloadService>().apply {
                setInputData(
                    Data.Builder().apply {
                        putString("book_id", book.id)
                    }.build()
                )
            }.build()
            WorkManager.getInstance(requireContext()).enqueueUniqueWork(
                "download-service", ExistingWorkPolicy.KEEP, work
            )
        }
    }

    private fun BookFragmentBinding.configureMenuButton() {
        menuImageView.setOnClickListener {
            MenuDialog(menuAdapter).show(requireActivity().supportFragmentManager, null)
        }
    }

    private fun BookFragmentBinding.configureGroupAdapter() {
        chapterListView.adapter = GroupAdapter(
            listOf(),
            {
                viewModel.selectedGroup.value = it
                findNavController().navigate(
                    BookFragmentDirections.actionBookFragmentToChapterFragment()
                )
            }
        ) {
            GroupDetailsDialog(it)
                .show(requireActivity().supportFragmentManager, null)
        }
    }

    private fun BookFragmentBinding.configureMetadata(
        it: Resource<Metadata?>,
        book: Book
    ) {
        when (it) {
            is Resource.Loading -> {
                metaProgress.visible = true
                notificationDisabled.visible = false
            }
            is Resource.Success -> {
                metaProgress.visible = false
                it.data?.apply {
                    bookImage.load(coverPath)
                    bookAuthor.text = author
                    bookDesc.text = description
                    bookRatingBar.rating = rating
                    bookRating.text = rating.toString()
                    bookGenre.text = category
                    bookGenre.visible = true
                    notificationDisabled.visible = !enableNotification

                    configureMenu(book, this)
                }
            }
            is Resource.Error -> {
                metaProgress.visible = false
                notificationDisabled.visible = false
                root.snackBar("Failed to load metadata")
            }
        }
    }

    private fun configureMenu(book: Book, metadata: Metadata? = null) {
        val menuItems: MutableList<MenuDialogItem> = mutableListOf(
            MenuDialogItem("Check for updates", R.drawable.refresh) {
                lifecycleScope.launchWhenResumed {
                    loadGroups(book, true, true)
                }
            },
            MenuDialogItem("Reload book data", R.drawable.cloud_download) {
                lifecycleScope.launchWhenResumed {
                    viewModel.getMetadata(requireContext(), book, true)
                        .observe(viewLifecycleOwner) {
                            binding?.configureMetadata(it, book)
                            it.data?.apply {
                                configureMenu(book, this)
                            }
                        }
                }
            },
            MenuDialogItem("Mark all chapters as read", R.drawable.check) {
                lifecycleScope.launchWhenResumed {
                    book.markAsRead(requireContext())
                }
            }
        )

        if (metadata != null) {
            val (action, drawable) = if (metadata.enableNotification) ("Disable" to R.drawable.bell_slash)
            else ("Enable" to R.drawable.bell)

            menuItems.add(
                MenuDialogItem("$action notifications", drawable) {
                    lifecycleScope.launchWhenResumed {
                        book.setNotifications(requireContext(), !metadata.enableNotification)
                    }
                }
            )
        }

        menuAdapter.submitList(menuItems)
    }

    private fun loadGroups(
        book: Book,
        refresh: Boolean = false,
        webNovelRefresh: Boolean = false
    ) {
        viewModel.getChapters(requireContext(), book, refresh, webNovelRefresh)
            .observe(viewLifecycleOwner) { resource ->
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
                            Snackbar.make(root, R.string.error_refreshing, Snackbar.LENGTH_SHORT)
                                .show()
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
