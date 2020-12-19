package com.ubadahj.qidianundergroud.ui.main

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookListFragmentBinding
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.ui.adapters.BookAdapter
import com.ubadahj.qidianundergroud.ui.adapters.FastScrollAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.BookItem
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem

class LibraryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val onBookSelected: (View?, IAdapter<BookItem>, BookItem, Int) -> Boolean =
            { _, _, item, _ ->
                viewModel.selectedBook = item.book
                findNavController().navigate(
                        LibraryFragmentDirections.actionLibraryFragmentToBookFragment()
                )
                true
            }
    private val menu = MenuDialog(
            MenuAdapter().apply {
                submitList(listOf(
                        MenuDialogItem("History", R.drawable.archive),
                        MenuDialogItem("Settings", R.drawable.settings),
                        MenuDialogItem("About", R.drawable.info)
                ))
            }
    ) { _, i, _ ->
        when (i) {
            0 -> {
            }
            1 -> {
            }
            2 -> {
                com.mikepenz.aboutlibraries.LibsBuilder().start(requireContext())
            }
        }
    }

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

            bookListingView.layoutManager = LinearLayoutManager(requireContext())
            viewModel.libraryBooks(requireContext()).observe(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        progressBar.visibility = View.GONE
                        bookListingView.adapter = FastScrollAdapter<BookItem>().wrap(
                                FastAdapter.with(BookAdapter(it.data!!)).apply {
                                    onClickListener = onBookSelected
                                }
                        )
                    }
                    is Resource.Loading -> {
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                binding?.apply {
                    val searchBarVisible = bookListingView.y != searchBar.root.y
                    bookListingView.animate()
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
