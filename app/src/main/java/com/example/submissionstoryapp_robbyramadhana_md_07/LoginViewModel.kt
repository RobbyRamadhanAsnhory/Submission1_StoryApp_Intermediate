package com.example.submissionstoryapp_robbyramadhana_md_07

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference) : ViewModel() {

    fun saveUser(user: UserModel) {
        viewModelScope.launch {
            pref.saveUser(UserModel(user.userId, user.name, user.token, user.isLogin))
        }
    }

}