package com.rockettsttudio.eatsease.data.models

import com.google.gson.annotations.SerializedName

data class RecipeFeed(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("ingredients") val ingredients: String,
    @SerializedName("instructions") val instructions: String,
    @SerializedName("created_at") val createdAt: String
)
