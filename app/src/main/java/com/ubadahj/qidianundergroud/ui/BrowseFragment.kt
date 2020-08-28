package com.ubadahj.qidianundergroud.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.databinding.BrowseFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.ui.adapters.BookListingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketException

class BrowseFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: BrowseFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BrowseFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar.appbar)
            toolbar.appbar.title = resources.getText(R.string.browse)
            bookListingView.layoutManager = LinearLayoutManager(requireContext())
            progressBar.visibility = View.VISIBLE
        }
        if (viewModel.bookList == null) {
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    viewModel.bookList = Api.getBooks(true)
                    updateListing(viewModel.bookList!!)
                } catch (e: SocketException) {
                    Snackbar.make(view, R.string.error_refreshing, Snackbar.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Snackbar.make(view, R.string.error_refreshing, Snackbar.LENGTH_SHORT).show()
                }
            }
        } else updateListing(viewModel.bookList!!)

    }

    private fun updateListing(books: List<Book>) {
        binding?.progressBar?.visibility = View.GONE
        binding?.bookListingView?.adapter = BookListingAdapter(books) {
            viewModel.updateSelectedBook(books[it])
            findNavController().navigate(
                BrowseFragmentDirections.actionBrowseFragmentToBookFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}