package com.rockettsttudio.eatsease.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rockettsttudio.eatsease.data.models.SavedRecipes

class SavedRecipeRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
    private val recipesRef: DatabaseReference? = userId?.let {
        database.getReference("users").child(it).child("favorites")
    }

    fun getSavedRecipes(): LiveData<List<SavedRecipes>> {
        val savedRecipesLiveData = MutableLiveData<List<SavedRecipes>>()

        recipesRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val savedRecipes = mutableListOf<SavedRecipes>()
                for (recipeSnapshot in dataSnapshot.children) {
                    val recipe = recipeSnapshot.getValue(SavedRecipes::class.java)
                    recipe?.let { savedRecipes.add(it) }
                }
                savedRecipesLiveData.value = savedRecipes
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

        return savedRecipesLiveData
    }

}