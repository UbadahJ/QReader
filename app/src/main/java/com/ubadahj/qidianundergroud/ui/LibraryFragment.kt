package com.ubadahj.qidianundergroud.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.database.DatabaseInstance
import com.ubadahj.qidianundergroud.databinding.LibraryFragmentBinding
import com.ubadahj.qidianundergroud.ui.adapters.BookListingAdapter

class LibraryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: LibraryFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LibraryFragmentBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        binding?.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar.appbar)
            toolbar.appbar.title = resources.getText(R.string.library)
            floatingButton.setOnClickListener {
                findNavController().navigate(
                    LibraryFragmentDirections.actionLibraryFragmentToBrowseFragment()
                )
            }
            bookListingView.layoutManager = LinearLayoutManager(requireContext())
            bookListingView.adapter = BookListingAdapter(
                DatabaseInstance.getInstance(requireContext()).get()
            ) {
                viewModel.updateSelectedBook(it)
                findNavController().navigate(
                    LibraryFragmentDirections.actionLibraryFragmentToBookFragment()
                )
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
            R.id.menu -> true
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.library_menu, menu)
    }

}