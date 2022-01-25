package com.ubadahj.qidianundergroud.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.repositories.MetadataRepository
import com.ubadahj.qidianundergroud.services.DownloadService
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.collectNotNull
import com.ubadahj.qidianundergroud.utils.models.isRead
import com.ubadahj.qidianundergroud.utils.ui.snackBar
import com.ubadahj.qidianundergroud.utils.ui.toDp
import com.ubadahj.qidianundergroud.utils.ui.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BookFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var groupJob: Job? = null
    private var binding: BookFragmentBinding? = null
    private val menuAdapter = MenuAdapter(listOf())

    @Inject
    lateinit var bookRepo: BookRepository

    @Inject
    lateinit var groupRepo: GroupRepository

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
        binding?.apply {
            readLatestButton.visible = false
            materialCardView.setOnClickListener {
                findNavController().navigate(
                    BookFragmentDirections.actionBookFragmentToBookChaptersFragment()
                )
            }
        }
        lifecycleScope.launch {
            viewModel.selectedBook.flowWithLifecycle(lifecycle).collectNotNull { init(it) }
        }
    }

    private fun init(book: Book) {
        configureMenu(book)
        binding?.apply {
            bookTitle.text = book.name
            bookImage.load(R.drawable.placeholder_600_800)

            configureLibraryButton(book)
            configureDownloadButton(book)
            configureMenuButton()

            if (book.author == null)
                loadGroups(book, refresh = true, webNovelRefresh = true)
            else
                loadGroups(book)

            configureMetadata(book)
        }
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
            lifecycleScope.launch {
                if (book.inLibrary) {
                    bookRepo.removeFromLibrary(book)
                    root.snackBar("Removed book to the library")
                } else {
                    bookRepo.addToLibrary(book)
                    root.snackBar("Added book to the library")
                }
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
                        putInt("book_id", book.id)
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

    private fun BookFragmentBinding.configureMetadata(
        book: Book
    ) {
        book.coverPath?.let {
            bookImage.load(it) {
                transformations(RoundedCornersTransformation(4.toDp(requireContext()).toFloat()))
            }
        }
        bookAuthor.text = book.author ?: "Unknown"
        bookDesc.text = book.description ?: "No description"
        bookRatingBar.rating = book.rating ?: 0.0f
        bookRating.text = book.rating?.toString() ?: "0.0"
        bookGenre.text = book.category ?: "Unknown"
        bookGenre.visible = true
        configureMenu(book)
    }

    private fun configureMenu(book: Book) {
        val menuItems: MutableList<MenuDialogItem> = mutableListOf(
            MenuDialogItem("Check for updates", R.drawable.refresh) {
                lifecycleScope.launch {
                    loadGroups(book, true, true)
                }
            },
            MenuDialogItem("Reload book data", R.drawable.cloud_download) {
                lifecycleScope.launch {
                    viewModel.getChapters(book, true)
                        .flowWithLifecycle(lifecycle)
                        .collect {
                            binding?.configureMetadata(book)
                            configureMenu(book)
                        }
                }
            },
            MenuDialogItem("Mark all chapters as read", R.drawable.check) {
                lifecycleScope.launch {
                    bookRepo.markAllRead(book)
                }
            }
        )

        if (book.link != null) {
            menuItems.add(
                MenuDialogItem("Open Webnovel Page", R.drawable.info) {
                    try {
                        requireActivity().startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://webnovel.com${book.link}".toUri()
                            )
                        )
                    } catch (e: Exception) {
                        binding?.root?.snackBar("Failed to open link")
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
        groupJob?.cancel()
        groupJob = lifecycleScope.launch {
            viewModel.getChapters(book, refresh, webNovelRefresh).collect {
                binding?.configureGroups(it)
            }
        }
    }

    private fun BookFragmentBinding.configureGroups(resource: Resource<List<Group>>) {
        when (resource) {
            Resource.Loading -> {
                loadingProgress.visible = true
                materialCardView.visible = false
            }
            is Resource.Success -> {
                loadingProgress.visible = false
                materialCardView.visible = true
                readLatestButton.apply {
                    val latestChapter = resource.data
                        .filter { !it.isRead() }
                        .minByOrNull { it.firstChapter }
                        ?.also { group ->
                            text = when {
                                group.lastRead != 0 -> "Read chapter ${group.lastRead}"
                                else -> "Read chapter ${group.firstChapter}"
                            }
                            setOnClickListener {
                                viewModel.setSelectedGroup(group)
                                findNavController().navigate(
                                    BookFragmentDirections.actionBookFragmentToChapterFragment()
                                )
                            }
                        }

                    visible = latestChapter != null
                }
                previewText.text = "Latest: ${
                    resource.data.sortedByDescending { it.firstChapter }.first().text
                }"
            }
            is Resource.Error -> {
                loadingProgress.visible = false
                materialCardView.visible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
