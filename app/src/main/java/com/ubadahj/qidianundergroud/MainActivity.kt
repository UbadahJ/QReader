package com.ubadahj.qidianundergroud

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActivityNavigator
import androidx.navigation.findNavController
import com.ubadahj.qidianundergroud.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()

    override fun finish() {
        super.finish()
        ActivityNavigator.applyPopAnimationsToPendingTransition(this)
    }

}