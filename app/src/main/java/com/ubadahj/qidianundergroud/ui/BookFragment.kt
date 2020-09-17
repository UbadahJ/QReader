package com.ubadahj.qidianundergroud.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.squareup.moshi.JsonDataException
import com.ubadahj.qidianundergroud.R
import com.ubadahj.qidianundergroud.api.Api
import com.ubadahj.qidianundergroud.database.Database
import com.ubadahj.qidianundergroud.database.DatabaseInstance
import com.ubadahj.qidianundergroud.databinding.BookFragmentBinding
import com.ubadahj.qidianundergroud.databinding.ChapterItemBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.SocketException

class BookFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()
    private val api = Api(true)

    private var binding: BookFragmentBinding? = null
    private lateinit var database: Database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BookFragmentBinding.inflate(inflater, container, false)
        viewModel.selectedBook.observe(viewLifecycleOwner) {
            if (it != null) init(it)
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = DatabaseInstance.getInstance(requireContext())
        viewModel.selectedBook.value?.apply {
            init(this)
        }
    }

    private fun init(book: Book) {
        var snackbar: Snackbar? = null
        binding?.apply {
            snackbar = Snackbar.make(root, R.string.error_refreshing, Snackbar.LENGTH_SHORT)
            header.text = book.name
            lastUpdated.text = if (book.status)
                "${resources.getString(R.string.chapter)}: Completed"
            else
                "${resources.getString(R.string.last_updated)}: ${book.formattedLastUpdated}"
            chapterListView.layoutManager = GridLayoutManager(requireContext(), 2)
            libraryButton.setOnClickListener {
                database.add(book)
                Snackbar.make(root, "Added book to the library", Snackbar.LENGTH_SHORT).show()
                libraryButton.visibility = View.GONE
            }
            if (database.get().contains(book))
                libraryButton.visibility = View.GONE
        }
        GlobalScope.launch(Dispatchers.Main) {
            try {
                book.chapterGroups = api.getChapters(book)
                binding?.chapterListView?.adapter = ChapterListingAdapter(book) {
                    book.lastRead = it.lastChapter
                    database.save()
                    CustomTabsIntent.Builder()
                        .build()
                        .launchUrl(requireContext(), it.link.toUri())
                    binding?.chapterListView?.adapter?.notifyDataSetChanged()
                }
            } catch (e: SocketException) {
                snackbar?.show()
            } catch (e: JsonDataException) {
                snackbar?.show()
            } catch (e: IOException) {
                snackbar?.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    class ChapterListingAdapter(
        private val book: Book,
        private val onClick: (ChapterGroup) -> Unit
    ) : RecyclerView.Adapter<ChapterListingAdapter.ViewHolder>() {

        private val groups: List<ChapterGroup> = book.chapterGroups
        private var defaultColor: Int = 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                ChapterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            if (defaultColor == 0)
                defaultColor = binding.root.currentTextColor
            return ViewHolder(binding, groups, onClick)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val context = holder.binding.root.context
            holder.binding.root.text = groups[position].text
            if (book.lastRead in groups[position])
                holder.binding.root.setTextColor(
                    ContextCompat.getColor(context, R.color.colorAccent)
                )
            else
                holder.binding.root.setTextColor(defaultColor)
        }

        override fun getItemCount(): Int = groups.size

        class ViewHolder(
            val binding: ChapterItemBinding,
            groups: List<ChapterGroup>,
            onClick: (ChapterGroup) -> Unit
        ) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener { onClick(groups[adapterPosition]) }
            }
        }
    }

}