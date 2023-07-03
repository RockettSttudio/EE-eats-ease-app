package com.rockettsttudio.eatsease.data.models

data class SavedRecipes(
    var recipeTitle: String = "",
    var imgUrl: String = "",
    var recipeSummary: String = "",
    var sourceURL: String = "",
    var recipeIngredients: String = "",
    var recipeInstructions: String = ""
) {
    // Add default constructor
    constructor() : this("", "", "", "", "", "")
}


