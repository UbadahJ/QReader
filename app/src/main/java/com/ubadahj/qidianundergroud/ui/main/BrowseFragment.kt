package com.ubadahj.qidianundergroud.ui.main

import android.os.Bundle
import android.text.Editable
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajalt.timberkt.d
import com.google.android.material.snackbar.Snackbar
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookListFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.ui.adapters.BookAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem

class BrowseFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val menu = MenuDialog(
        MenuAdapter(
            listOf(
                MenuDialogItem("Refresh", R.drawable.refresh) {
                    viewModel
                        .getBooks(requireContext(), refresh = true)
                        .observe(viewLifecycleOwner, this@BrowseFragment::getBooks)
                }
            )
        )
    )

    private var binding: BookListFragmentBinding? = null
    private val adapter: BookAdapter = BookAdapter(listOf()) {
        viewModel.selectedBook.value = it
        findNavController().navigate(
            BrowseFragmentDirections.actionBrowseFragmentToBookFragment()
        )
    }

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
            bookListingView.adapter = adapter
            searchBar.searchEditText.addTextChangedListener { text: Editable? ->
                adapter.filter.filter((text))
            }
        }
    }

    private fun getBooks(resource: Resource<List<Book>>) {
        d { "getBooks(): resource = $resource" }
        when (resource) {
            is Resource.Success -> {
                binding?.progressBar?.visibility = View.GONE
                adapter.submitList(resource.data!!)
            }
            is Resource.Loading -> binding?.progressBar?.visibility = View.VISIBLE
            is Resource.Error -> {
                binding?.apply {
                    progressBar.visibility = View.GONE
                    Snackbar.make(root, R.string.error_refreshing, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
