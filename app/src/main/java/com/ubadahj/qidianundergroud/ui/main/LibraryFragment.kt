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
import com.ubadahj.qidianundergroud.preferences.LibraryPreferences
import com.ubadahj.qidianundergroud.ui.adapters.LibraryAdapter
import com.ubadahj.qidianundergroud.ui.adapters.decorations.GridItemOffsetDecoration
import com.ubadahj.qidianundergroud.ui.dialog.AboutDialog
import com.ubadahj.qidianundergroud.utils.ui.isPortraitMode
import com.ubadahj.qidianundergroud.utils.ui.removeAllDecorations
import com.ubadahj.qidianundergroud.utils.ui.toDp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    @Inject
    lateinit var pref: LibraryPreferences

    private val viewModel: MainViewModel by activityViewModels()
    private val adapter: LibraryAdapter = LibraryAdapter(
        listOf()
    ) {
        viewModel.setSelectedBook(it)
        findNavController().navigate(
            LibraryFragmentDirections.actionLibraryFragmentToBookFragment()
        )
    }

    private var binding: BookListFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return BookListFragmentBinding.inflate(inflater, container, false).apply {
            binding = this
            viewModel.clearState()
        }.root
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
            lifecycleScope.launch {
                bookListingView.adapter = adapter
                pref.columnCountPortrait.asFlow()
                    .combine(pref.columnCountLandscape.asFlow()) { port, land ->
                        if (isPortraitMode()) port else land
                    }
                    .flowWithLifecycle(lifecycle)
                    .collect {
                        bookListingView.layoutManager = GridLayoutManager(requireContext(), it)
                        bookListingView.removeAllDecorations()
                        bookListingView.addItemDecoration(
                            GridItemOffsetDecoration(
                                it, 12.toDp(requireContext()).toInt()
                            )
                        )
                    }
            }

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
            R.id.menu_settings -> {
                findNavController().navigate(
                    LibraryFragmentDirections.actionLibraryFragmentToSettingFragment()
                )
                true
            }
            R.id.menu_about -> {
                AboutDialog().show(requireActivity().supportFragmentManager, null)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.library_menu, menu)
    }
}
