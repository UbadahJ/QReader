package com.ubadahj.qidianundergroud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.findNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ubadahj.qidianundergroud.databinding.MainActivityBinding
import com.ubadahj.qidianundergroud.services.NotificationWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding
    private val notificationRequest =
        PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
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