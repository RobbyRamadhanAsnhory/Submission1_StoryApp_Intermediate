package com.example.submissionstoryapp_robbyramadhana_md_07

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val loginResult: UserModel
)
