package com.ubadahj.qidianundergroud

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigator
import androidx.navigation.findNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.ajalt.timberkt.Timber
import com.ubadahj.qidianundergroud.databinding.MainActivityBinding
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.ChapterGroupRepository
import com.ubadahj.qidianundergroud.services.NotificationWorker
import com.ubadahj.qidianundergroud.services.updater.service.UpdateService
import com.ubadahj.qidianundergroud.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val updateRequest = OneTimeWorkRequestBuilder<UpdateService>().build()
    private val notificationRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
        1, TimeUnit.HOURS
    ).build()

    private lateinit var binding: MainActivityBinding


    @Inject
    lateinit var bookRepo: BookRepository

    @Inject
    lateinit var groupRepo: ChapterGroupRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        lifecycleScope.launch {
            val book = intent.extras?.getString("book")?.apply {
                viewModel.selectedBook.value = bookRepo.getBookById(this).first()
                viewModel.selectedChapter.value = null
            }
            val groups = intent.extras?.getString("group")?.apply {
                viewModel.selectedGroup.value = groupRepo.getGroupByLink(this).first()
            }

            if (book != null) {
                val navHost = binding.navHostFragment.findNavController()
                val graphInflater = navHost.navInflater
                navHost.graph = graphInflater.inflate(R.navigation.nav_graph).apply {
                    startDestination =
                        if (groups != null) R.id.chapterFragment else R.id.bookFragment
                }
            }
        }

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "NotificationService", ExistingPeriodicWorkPolicy.REPLACE, notificationRequest
        )
        WorkManager.getInstance(applicationContext).enqueue(updateRequest)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

}
