package com.rockettsttudio.eatsease.data.network

import com.rockettsttudio.eatsease.data.models.Recipe

data class ApiResponse(
    val recipes: List<Recipe>
)
