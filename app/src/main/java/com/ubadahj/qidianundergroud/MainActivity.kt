package com.ubadahj.qidianundergroud

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.ajalt.timberkt.Timber
import com.ubadahj.qidianundergroud.databinding.MainActivityBinding
import com.ubadahj.qidianundergroud.models.Book
import com.ubadahj.qidianundergroud.models.ChapterGroup
import com.ubadahj.qidianundergroud.services.NotificationWorker
import com.ubadahj.qidianundergroud.ui.MainViewModel
import kotlinx.android.synthetic.main.main_activity.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val viewModel: MainViewModel by viewModels()
    private val notificationRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        val book = intent.extras?.get("book") as Book?
        val chapters = intent.extras?.get("chapters") as ChapterGroup?
        viewModel.selectedBook.value = book
        viewModel.selectedChapter.value = chapters

        if (book != null) {
            val navHost = nav_host_fragment as NavHostFragment
            val graphInflater = navHost.navController.navInflater
            navHost.navController.graph = graphInflater.inflate(R.navigation.nav_graph).apply {
                startDestination = if (chapters != null) R.id.chapterFragment else R.id.bookFragment
            }
        }

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "NotificationService", ExistingPeriodicWorkPolicy.KEEP, notificationRequest
        )
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

}