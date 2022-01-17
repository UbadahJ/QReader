package com.ubadahj.qidianundergroud.ui.main

import android.os.Bundle
import android.text.Editable
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookListFragmentBinding
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.repositories.MetadataRepository
import com.ubadahj.qidianundergroud.ui.adapters.LibraryAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.adapters.decorations.GridItemOffsetDecoration
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.ui.toDp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    @Inject
    lateinit var groupRepo: GroupRepository

    @Inject
    lateinit var metadataRepo: MetadataRepository

    private val viewModel: MainViewModel by activityViewModels()
    private val adapter: LibraryAdapter = LibraryAdapter(
        listOf()
    ) {
        viewModel.setSelectedBook(it)
        findNavController().navigate(
            LibraryFragmentDirections.actionLibraryFragmentToBookFragment()
        )
    }
    private val menu = MenuDialog(
        MenuAdapter(
            listOf(
                MenuDialogItem("History", R.drawable.archive),
                MenuDialogItem("Settings", R.drawable.settings),
                MenuDialogItem("About", R.drawable.info) {
                    com.mikepenz.aboutlibraries.LibsBuilder().start(requireContext())
                }
            )
        )
    )

    private var binding: BookListFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BookListFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding?.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar.appbar)
            toolbar.appbar.title = resources.getText(R.string.library)
            floatingButton.visibility = View.VISIBLE
            floatingButton.setOnClickListener {
                findNavController().navigate(
                    LibraryFragmentDirections.actionLibraryFragmentToBrowseFragment()
                )
            }

            bookListingView.adapter = adapter
            bookListingView.layoutManager = GridLayoutManager(requireContext(), 2)
            bookListingView.addItemDecoration(
                GridItemOffsetDecoration(
                    2, 12.toDp(requireContext()).toInt()
                )
            )

            searchBar.searchEditText.addTextChangedListener { text: Editable? ->
                adapter.filter.filter((text))
            }
            lifecycleScope.launch {
                viewModel.libraryBooks.flowWithLifecycle(lifecycle).collect {
                    when (it) {
                        is Resource.Success -> {
                            progressBar.visibility = View.GONE
                            adapter.submitList(it.data)
                        }
                        Resource.Loading -> {
                            progressBar.visibility = View.VISIBLE
                        }
                        is Resource.Error -> {
                            Snackbar.make(
                                root,
                                "CRITICAL: Failed to fetch data from internal DB",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                binding?.apply {
                    val searchBarVisible = bookListingViewContainer.y != searchBar.root.y
                    bookListingViewContainer.animate()
                        .alpha(1f)
                        .translationY(if (!searchBarVisible) searchBar.root.height + 32f else 0f)
                        .start()
                }
                true
            }
            R.id.menu -> {
                menu.show(requireActivity().supportFragmentManager, null)
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
    }
}
