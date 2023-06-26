package com.rockettsttudio.eatsease.ui.recipes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rockettsttudio.eatsease.data.models.Recipe
import com.rockettsttudio.eatsease.repositories.RecipeRepository

class RecipeViewModel(application: Application) : AndroidViewModel(application) {
    private val recipeRepository: RecipeRepository = RecipeRepository()

    private val _randomRecipes = MutableLiveData<List<Recipe>>()
    val randomRecipes: LiveData<List<Recipe>> get() = _randomRecipes

    fun setRecipes(recipes: List<Recipe>){
        _randomRecipes.value = recipes
    }

    fun fetchRandomRecipes(apiKey: String, number: Int, tags: String) {
        recipeRepository.getRandomRecipes(
            apiKey,
            number,
            tags,
            { recipes ->
                _randomRecipes.value = recipes
            },
            { error ->
                // Handle error
            }
        )
    }
}