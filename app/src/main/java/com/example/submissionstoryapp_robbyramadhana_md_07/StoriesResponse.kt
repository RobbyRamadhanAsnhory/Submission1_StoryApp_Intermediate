package com.example.submissionstoryapp_robbyramadhana_md_07

data class StoriesResponse(
    val error: Boolean,
    val message: String,
    val listStory: ArrayList<Story>
)
