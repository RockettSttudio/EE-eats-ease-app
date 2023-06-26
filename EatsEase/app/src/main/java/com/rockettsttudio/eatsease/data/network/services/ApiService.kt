package com.rockettsttudio.eatsease.data.network.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import com.rockettsttudio.eatsease.data.network.ApiResponse

interface ApiService {
    @GET("recipes/random")
    fun getRandomRecipes(
        @Query("apiKey") apiKey: String,
        @Query("number") number: Int,
        @Query("tag") tag: String? = null
    ): Call<ApiResponse>
}