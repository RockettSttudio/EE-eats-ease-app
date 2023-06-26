package com.rockettsttudio.eatsease.data.models

data class Recipe(
    val id: Int,
    val title: String,
    val image: String,
    val extendedIngredients: List<Ingredient>,
    val summary: String,
    val instructions: String
)
