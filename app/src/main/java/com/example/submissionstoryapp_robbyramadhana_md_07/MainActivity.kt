package com.example.submissionstoryapp_robbyramadhana_md_07

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionstoryapp_robbyramadhana_md_07.databinding.ActivityMainBinding

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
        setupViewModel()

        binding.fbAddstory.setOnClickListener {
            val intent = Intent(this@MainActivity, PostStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu_logout -> {
                mainViewModel.logout()
                finish()
                true
            }
            else -> false
        }
    }

    private fun initRecyclerView() {
        binding.rvPhotos.layoutManager = LinearLayoutManager(this)
        adapter = StoryAdapter()
        binding.rvPhotos.adapter = adapter
    }

    private fun setupViewModel() {
        val pref = UserPreference.getInstance(dataStore)
        mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref)
        )[MainViewModel::class.java]

        mainViewModel.getUser().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this@MainActivity, LandingPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            showLoading(true)
            mainViewModel.setStories(tokenAuth = user.token)
        }

        mainViewModel.getStories().observe(this) {
            if (it != null) {
                adapter.setStoryList(it)
                adapter.notifyDataSetChanged()
                showLoading(false)
            } else {
                showLoading(false)
                binding.tvNullData.visibility = View.VISIBLE
            }
        }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.pbMain.visibility = View.VISIBLE
        } else {
            binding.pbMain.visibility = View.GONE
        }
    }
}