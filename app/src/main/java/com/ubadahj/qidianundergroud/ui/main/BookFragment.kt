package com.ubadahj.qidianundergroud.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.load
import com.github.ajalt.timberkt.e
import com.google.android.material.snackbar.Snackbar
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Metadata
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository
import com.ubadahj.qidianundergroud.repositories.MetadataRepository
import com.ubadahj.qidianundergroud.services.DownloadService
import com.ubadahj.qidianundergroud.ui.adapters.GroupAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.dialog.GroupDetailsDialog
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.ui.snackBar
import com.ubadahj.qidianundergroud.utils.ui.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class BookFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: BookFragmentBinding? = null
    private var menuAdapter = MenuAdapter(listOf())

    @Inject
    lateinit var bookRepo: BookRepository

    @Inject
    lateinit var groupRepo: ChapterGroupRepository

    @Inject
    lateinit var metadataRepo: MetadataRepository

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
                viewModel.getMetadata(book).observe(viewLifecycleOwner) {
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
                    bookRepo.removeFromLibrary(book)
                    root.snackBar("Removed book to the library")
                } else {
                    bookRepo.addToLibrary(book)
                    root.snackBar("Added book to the library")
                }

                bookRepo
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
            GroupDetailsDialog(groupRepo, it)
                .show(requireActivity().supportFragmentManager, null)
        }
    }

    private fun BookFragmentBinding.configureMetadata(
        it: Resource<Metadata?>,
        book: Book
    ) {
        when (it) {
            Resource.Loading -> {
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
                    viewModel.getMetadata(book, true)
                        .observe(viewLifecycleOwner) {
                            binding?.configureMetadata(it, book)
                            (it as? Resource.Success<Metadata?>)?.data?.apply {
                                configureMenu(book, this)
                            }
                        }
                }
            },
            MenuDialogItem("Mark all chapters as read", R.drawable.check) {
                lifecycleScope.launchWhenResumed {
                    bookRepo.markAllRead(book)
                }
            }
        )

        if (metadata != null) {
            val (action, drawable) = if (metadata.enableNotification) ("Disable" to R.drawable.bell_slash)
            else ("Enable" to R.drawable.bell)

            menuItems.addAll(listOf(
                MenuDialogItem("$action notifications", drawable) {
                    lifecycleScope.launchWhenResumed {
                        metadataRepo.setNotifications(book, !metadata.enableNotification)
                    }
                },
                MenuDialogItem("Open Webnovel Page", R.drawable.info) {
                    try {
                        requireActivity().startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://webnovel.com${metadata.link}".toUri()
                            )
                        )
                    } catch (e: Exception) {
                        binding?.root?.snackBar("Failed to open link")
                    }
                }
            ))
        }

        menuAdapter.submitList(menuItems)
    }

    private fun loadGroups(
        book: Book,
        refresh: Boolean = false,
        webNovelRefresh: Boolean = false
    ) {
        viewModel.getChapters(book, refresh, webNovelRefresh)
            .observe(viewLifecycleOwner) { resource ->
                binding?.apply {
                    when (resource) {
                        is Resource.Success -> {
                            materialCardView.visibility = View.VISIBLE
                            loadingProgress.visibility = View.GONE
                            (chapterListView.adapter as? GroupAdapter)
                                ?.submitList(resource.data!!)
                        }
                        Resource.Loading -> {
                            loadingProgress.visibility = View.VISIBLE
                            materialCardView.visibility = View.GONE
                        }
                        is Resource.Error -> {
                            e(resource.message)
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
