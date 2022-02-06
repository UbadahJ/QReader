package com.ubadahj.qidianundergroud.ui.viewmodels

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ajalt.timberkt.Timber
import com.ubadahj.qidianundergroud.models.Content
import com.ubadahj.qidianundergroud.models.Group
import com.ubadahj.qidianundergroud.models.Resource
import com.ubadahj.qidianundergroud.repositories.ContentRepository
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.utils.coroutines.asSourceFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException
import javax.inject.Inject

@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val groupRepo: GroupRepository,
    private val contentRepo: ContentRepository
) : ViewModel() {

    private val _group = MutableStateFlow<Group?>(null).asSourceFlow()
    val group = _group.asStateFlow()

    private val _contents = MutableStateFlow<Resource<List<Content>>>(Resource.Loading)
    val contents = _contents.asStateFlow()

    private val _selectedContent = MutableStateFlow<Content?>(null)
    val selectedContent = _selectedContent.asStateFlow()

    fun init(link: String) {
        viewModelScope.launch {
            launch { _group.emitAll(groupRepo.getGroupByLink(link)) }
            group.collect {
                it?.let { getContents() }
            }
        }
    }

    fun getContents() {
        viewModelScope.launch {
            _contents.emitAll(getContentsAsFlow())
        }
    }

    fun updateLastRead(content: Content) {
        group.value?.let {
            groupRepo.updateLastRead(it, content.getIndex())
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun getContentsAsFlow() = flow {
        emit(Resource.Loading)
        try {
            emitAll(
                contentRepo.getContents(group = group.value!!) {
                    WebView(it).apply { settings.javaScriptEnabled = true }
                }.catch { Resource.Error(it) }.map { Resource.Success(it) }
            )
        } catch (e: TimeoutException) {
            Timber.e(e) { "Failed loading content" }
            emit(Resource.Error(e))
        }
    }

    fun setSelectedContent(content: Content) {
        _selectedContent.value = content
    }

    private fun Content.getIndex(): Int {
        return try {
            title.split(':').first().trim().split(" ").last().toInt()
        } catch (e: RuntimeException) {
            group.value?.lastChapter?.toInt() ?: throw IllegalStateException(
                "Failed to get lastChapter from ViewModel selectChapterGroup"
            )
        }
    }

}