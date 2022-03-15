package com.ubadahj.qidianundergroud.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ubadahj.qidianundergroud.databinding.BookReviewFragmentBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.ui.adapters.BookReviewAdapter
import com.ubadahj.qidianundergroud.ui.viewmodels.MainViewModel
import com.ubadahj.qidianundergroud.utils.coroutines.SingleJobScope
import com.ubadahj.qidianundergroud.utils.ui.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookReviewFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private var binding: BookReviewFragmentBinding? = null

    private val scope = SingleJobScope(lifecycleScope)
    private val adapter = BookReviewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return BookReviewFragmentBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val book = viewModel.selectedBook.value
        if (book == null) {
            findNavController().popBackStack()
            return
        }

        binding?.apply {
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            collapsingLayout.title = "Reviews"
            reviewListView.adapter = adapter

            errorLayout.errorButton.setOnClickListener {
                getReviews(book)
            }
        }

        getReviews(book)
    }

    private fun getReviews(book: Book) {
        lifecycleScope.launch {
            viewModel.getReviews(book).collect {
                when (it) {
                    is Resource.Error -> {
                        binding?.progressBar?.visible = false
                        binding?.errorLayout?.root?.visible = true
                    }
                    Resource.Loading -> {
                        binding?.progressBar?.visible = true
                        binding?.errorLayout?.root?.visible = false
                    }
                    is Resource.Success -> {
                        binding?.progressBar?.visible = false
                        binding?.errorLayout?.root?.visible = false
                        adapter.submitList(it.data)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}