package com.example.submissionstoryapp_robbyramadhana_md_07

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.submissionstoryapp_robbyramadhana_md_07.databinding.ActivityStoryDetailBinding
import com.example.submissionstoryapp_robbyramadhana_md_07.Complement.withDateFormat

class StoryDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setupView()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setupView() {
        val story = intent.getParcelableExtra<Story>("Story") as Story
        binding.nameTextView.text = story.name
        binding.dateTextView.text = getString(R.string.date, story.createdAt.withDateFormat())
        binding.descTextView.text = story.description

        Glide.with(this)
            .load(story.photoUrl)
            .into(binding.previewImageView)
    }
}