package com.example.submissionstoryapp_robbyramadhana_md_07

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val pref: UserPreference) : ViewModel() {

    val listStory = MutableLiveData<ArrayList<Story>?>()

    fun setStories(tokenAuth: String) {
        Log.d(this@MainViewModel::class.java.simpleName, tokenAuth)
        ApiConfig().getApiService().getStories(token = "Bearer $tokenAuth")
            .enqueue(object : Callback<StoriesResponse> {
                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    listStory.postValue(null)
                }

                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>
                ) {
                    if (response.code() == 200) {
                        listStory.postValue(response.body()?.listStory)
                    } else {
                        listStory.postValue(null)
                    }
                }

            })
    }

    fun getStories(): MutableLiveData<ArrayList<Story>?> {
        return listStory
    }

    fun getUser(): LiveData<UserModel> {
        return pref.getUser().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }

}