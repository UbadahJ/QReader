package com.ubadahj.qidianundergroud

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.ActivityNavigator
import androidx.navigation.findNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.github.ajalt.timberkt.Timber
import com.ubadahj.qidianundergroud.databinding.MainActivityBinding
import com.ubadahj.qidianundergroud.preferences.AppearancePreferences
import com.ubadahj.qidianundergroud.preferences.LibraryPreferences
import com.ubadahj.qidianundergroud.repositories.BookRepository
import com.ubadahj.qidianundergroud.repositories.GroupRepository
import com.ubadahj.qidianundergroud.services.NotificationWorker
import com.ubadahj.qidianundergroud.services.updater.service.UpdateService
import com.ubadahj.qidianundergroud.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val updateRequest = OneTimeWorkRequestBuilder<UpdateService>().build()

    private lateinit var binding: MainActivityBinding

    @Inject
    lateinit var appearancePref: AppearancePreferences

    @Inject
    lateinit var libraryPref: LibraryPreferences

    @Inject
    lateinit var bookRepo: BookRepository

    @Inject
    lateinit var groupRepo: GroupRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())

        lifecycleScope.launch {
            val book = intent.extras?.getInt("book")?.let {
                viewModel.setSelectedBook(it)
            }

            val groups = intent.extras?.getString("group")
                ?.let { groupRepo.getGroupByLink(it).first() }
                .also { viewModel.setSelectedGroup(it) }

            if (book != null) {
                val navHost = binding.navHostFragment.findNavController()
                val graphInflater = navHost.navInflater
                navHost.graph = graphInflater.inflate(R.navigation.nav_graph).apply {
                    startDestination =
                        if (groups != null) R.id.chapterFragment else R.id.bookFragment
                }
            }
        }

        val manager = WorkManager.getInstance(applicationContext)
        lifecycleScope.launch {
            launch {
                appearancePref.nightMode.asFlow().map(appearancePref::nightModeMapper)
                    .flowWithLifecycle(lifecycle)
                    .collect { AppCompatDelegate.setDefaultNightMode(it) }
            }
            launch {
                libraryPref.updateFrequency.asFlow().map(libraryPref::mapUpdateFrequency).collect {
                    val uniqueTag = getString(R.string.worker_library_notification)
                    it?.let { freq ->
                        manager.enqueueUniquePeriodicWork(
                            uniqueTag,
                            ExistingPeriodicWorkPolicy.REPLACE,
                            PeriodicWorkRequestBuilder<NotificationWorker>(
                                freq.first, freq.second
                            ).build()
                        )
                    } ?: manager.cancelUniqueWork(uniqueTag)
                }
            }
        }

        manager.enqueue(updateRequest)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

}
