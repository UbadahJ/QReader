package com.ubadahj.qidianundergroud.ui.main

import android.os.Bundle
import android.text.Editable
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookListFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.ui.adapters.BookAdapter
import com.ubadahj.qidianundergroud.ui.adapters.FastScrollAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.BookItem
import com.ubadahj.qidianundergroud.ui.adapters.items.MenuAdapterItem
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog

class BrowseFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val menu = MenuDialog(
        MenuAdapter(
            listOf(MenuAdapterItem("Refresh", R.drawable.refresh))
        )
    ).apply {
        adapter.onClickListener = { _, _, _, i ->
            when (i) {
                0 -> {
                    viewModel
                        .getBooks(requireContext(), refresh = true)
                        .observe(viewLifecycleOwner, this@BrowseFragment::getBooks)
                    true
                }
                else -> false
            }
        }
    }

    private var binding: BookListFragmentBinding? = null
    private var adapter: ItemAdapter<BookItem>? = null

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

        viewModel.getBooks(requireContext()).observe(viewLifecycleOwner, this::getBooks)

        binding?.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar.appbar)
            toolbar.appbar.title = resources.getText(R.string.browse)
            bookListingView.layoutManager = LinearLayoutManager(requireContext())
            searchBar.searchEditText.addTextChangedListener { text: Editable? ->
                adapter?.filter(text)
            }
        }
    }

    private fun getBooks(resource: Resource<List<Book>>) {
        d { "getBooks(): resource = $resource" }
        when (resource) {
            is Resource.Success -> updateListing(resource.data!!)
            is Resource.Loading -> binding?.progressBar?.visibility = View.VISIBLE
            is Resource.Error -> {
                binding?.apply {
                    progressBar.visibility = View.GONE
                    Snackbar.make(root, R.string.error_refreshing, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateListing(books: List<Book>) {
        binding?.apply {
            progressBar.visibility = View.GONE
            adapter = BookAdapter(books)
            bookListingView.adapter = FastScrollAdapter<BookItem>().wrap(
                    FastAdapter.with(adapter!!).apply {
                        onClickListener = { _, _, item, _ ->
                            viewModel.selectedBook = item.book
                            findNavController().navigate(
                                    BrowseFragmentDirections.actionBrowseFragmentToBookFragment()
                            )
                            false
                        }
                    }

            )
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
