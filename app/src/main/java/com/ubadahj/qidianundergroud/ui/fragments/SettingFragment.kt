package com.ubadahj.qidianundergroud.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.ubadahj.qidianundergroud.databinding.SettingBaseFragmentBinding

class SettingFragment : Fragment() {

    private var binding: SettingBaseFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return SettingBaseFragmentBinding.inflate(inflater, container, false).apply {
            (requireActivity() as? AppCompatActivity)?.apply {
                setSupportActionBar(toolbar.appbar)
            }
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            val controller = settingNavHostFragment.getFragment<NavHostFragment>().navController
            val configuration = AppBarConfiguration(setOf(), fallbackOnNavigateUpListener = {
                requireActivity().onBackPressed()
                true
            })
            toolbar.appbar.setupWithNavController(controller, configuration)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}