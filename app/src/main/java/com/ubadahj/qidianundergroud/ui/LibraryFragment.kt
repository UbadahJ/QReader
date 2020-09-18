package com.ubadahj.qidianundergroud.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.IAdapter
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.database.DatabaseInstance
import com.ubadahj.qidianundergroud.databinding.LibraryFragmentBinding
import com.ubadahj.qidianundergroud.ui.adapters.BookAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.adapters.items.BookItem
import com.ubadahj.qidianundergroud.utils.setListener

class LibraryFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val onBookSelected: (View?, IAdapter<BookItem>, BookItem, Int) -> Boolean =
        { _, _, item, _ ->
            viewModel.selectedBook.value = item.book
            findNavController().navigate(
                LibraryFragmentDirections.actionLibraryFragmentToBookFragment()
            )
            true
        }

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
            bookListingView.adapter = FastAdapter.with(
                BookAdapter(DatabaseInstance.getInstance(requireContext()).get())
            ).apply { onClickListener = onBookSelected }

            dropdownMenu.menu.layoutManager = LinearLayoutManager(requireContext())
            dropdownMenu.menu.adapter = MenuAdapter(listOf("History", "Settings", "About")) {}
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
                binding?.apply {
                    if (dropdownMenu.root.visibility == View.GONE) {
                        dropdownMenu.root.visibility = View.VISIBLE
                        dropdownMenu.root.animate().alpha(1f).setListener {
                        }.start()
                    } else {
                        dropdownMenu.root.animate().alpha(0f).setListener {
                            dropdownMenu.root.visibility = View.GONE
                        }.start()
                    }
                }
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.library_menu, menu)
    }

}