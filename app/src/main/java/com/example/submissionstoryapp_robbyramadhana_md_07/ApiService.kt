package com.example.submissionstoryapp_robbyramadhana_md_07

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("login")
    fun loginUser(@Body login: Login): Call<LoginResponse>

    @POST("register")
    fun registerUser(@Body register: Register): Call<SaveDataResponse>

    @GET("stories")
    fun getStories(@Header("Authorization") token: String): Call<StoriesResponse>

    @Multipart
    @POST("/v1/stories")
    fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") token: String
    ): Call<SaveDataResponse>

}