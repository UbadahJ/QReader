package com.ubadahj.qidianundergroud.ui.main

import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.github.ajalt.timberkt.d
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.databinding.BookListFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.services.IndexService
import com.ubadahj.qidianundergroud.ui.adapters.BookAdapter
import com.ubadahj.qidianundergroud.ui.adapters.MenuAdapter
import com.ubadahj.qidianundergroud.ui.dialog.MenuDialog
import com.ubadahj.qidianundergroud.ui.models.MenuDialogItem
import com.ubadahj.qidianundergroud.utils.ui.snackBar
import com.ubadahj.qidianundergroud.utils.ui.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BrowseFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val menu = MenuDialog(
        MenuAdapter(
            listOf(
                MenuDialogItem("Refresh", R.drawable.refresh) {
                    lifecycleScope.launch {
                        viewModel
                            .getBooks(refresh = true)
                            .flowWithLifecycle(lifecycle)
                            .collect { getBooks(it, true) }
                    }
                },
                MenuDialogItem("Generate Index", R.drawable.download) {
                    val work = OneTimeWorkRequestBuilder<IndexService>().build()
                    WorkManager.getInstance(requireContext()).enqueueUniqueWork(
                        "index-service", ExistingWorkPolicy.KEEP, work
                    )
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

        binding?.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar.appbar)
            toolbar.appbar.title = resources.getText(R.string.browse)
            bookListingView.layoutManager = LinearLayoutManager(requireContext())
            bookListingView.adapter = adapter
            searchBar.searchEditText.addTextChangedListener { text: Editable? ->
                adapter.filter.filter((text))
            }
            sortBySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    adapter.sortBy { it.name }
                    when (position) {
                        1 -> adapter.sortBy { it.author }
                        2 -> adapter.sortBy({ toString() }) { it.rating }
                        3 -> adapter.sortBy { it.lastUpdated }
                        4 -> adapter.sortBy { it.completed }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            descendingSwitch.isUseMaterialThemeColors = true
            descendingSwitch.setOnClickListener { adapter.reverse() }
        }

        lifecycleScope.launch {
            viewModel
                .getBooks()
                .flowWithLifecycle(lifecycle)
                .collect { getBooks(it) }
        }
    }

    private fun getBooks(
        resource: Resource<List<Book>>,
        isRefresh: Boolean = false
    ) {
        when (resource) {
            is Resource.Success -> {
                d { "getBooks: ${resource.data.size}" }
                binding?.apply {
                    progressBar.visible = false
                    if (isRefresh) {
                        val count = resource.data.size - adapter.currentList.size
                        if (count == 0)
                            root.snackBar("No new books found!")
                        else
                            root.snackBar("$count new books added")
                    }
                }

                binding?.apply {
                    adapter.submitList(
                        resource.data.run {
                            val list = when (sortBySpinner.selectedItemPosition) {
                                1 -> sortedBy { it.author }
                                2 -> sortedBy { it.rating }
                                3 -> sortedBy { it.lastUpdated }
                                4 -> sortedBy { it.completed }
                                else -> sortedBy { it.name }
                            }

                            d { "getBooks: list => ${list.size}" }
                            if (descendingSwitch.isChecked) list.reversed()
                            else list
                        }
                    )
                } ?: adapter.submitList(resource.data)
            }
            Resource.Loading -> binding?.progressBar?.visible = true
            is Resource.Error -> {
                binding?.apply {
                    progressBar.visible = false
                    root.snackBar(R.string.error_refreshing)
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
                        .translationY(if (!searchBarVisible) sortBySpinner.y + 32f else 0f)
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
