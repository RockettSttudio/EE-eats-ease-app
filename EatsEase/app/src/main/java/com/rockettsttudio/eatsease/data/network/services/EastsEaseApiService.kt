package com.rockettsttudio.eatsease.data.network.services
import com.rockettsttudio.eatsease.data.models.RecipeFeed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EastsEaseApiService {
    @GET("recipes")
    fun getPosts(): Call<List<RecipeFeed>>
}