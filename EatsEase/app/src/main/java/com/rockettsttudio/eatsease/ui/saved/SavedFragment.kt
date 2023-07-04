package com.rockettsttudio.eatsease.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.data.models.Recipe
import com.rockettsttudio.eatsease.data.models.SavedRecipes
import com.rockettsttudio.eatsease.databinding.FragmentSavedBinding
import com.rockettsttudio.eatsease.ui.MainActivity

class SavedFragment : Fragment(), SavedAdapter.OnItemClickListener {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!

    private lateinit var savedAdapter: SavedAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and adapter
        savedAdapter = SavedAdapter(this)
        binding.recyclerviewSaved.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewSaved.adapter = savedAdapter


        // Add your logic to fetch the list of saved recipes from the database
        // and pass it to the adapter using setRecipes() method
        val savedRecipes = getSavedRecipesFromDatabase()
        savedAdapter.setRecipes(savedRecipes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClick(recipe: SavedRecipes) {
        // Handle item click event
        val bundle = Bundle().apply {
            putString("title", recipe.recipeTitle)
            putString("image", recipe.imgUrl)
            val summary = HtmlCompat.fromHtml(recipe.recipeSummary, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            putString("summary", summary)
            putString("sourceURL", recipe.sourceURL)
            putString("ingredients", recipe.recipeIngredients)
            putString("instructions", recipe.recipeInstructions)
        }

        val mainActivity = activity as MainActivity
        mainActivity.setTopNavigationVisibility(View.GONE)
        // Navigate to the RecipeDetailFragment and pass the bundle
        val navController = findNavController()
        navController.navigate(R.id.action_navigation_saved_to_recipeDetailsFragment, bundle)
    }

    private fun getSavedRecipesFromDatabase(): List<SavedRecipes> {
        val database = FirebaseDatabase.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        val recipesRef = userId?.let { database.getReference("users").child(it).child("favorites") }
        val savedRecipes = mutableListOf<SavedRecipes>()

        recipesRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                savedRecipes.clear()
                for (recipeSnapshot in dataSnapshot.children) {
                    val recipe = recipeSnapshot.getValue(SavedRecipes::class.java)
                    recipe?.let { savedRecipes.add(it) }
                }
                savedAdapter.setRecipes(savedRecipes)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })

        return savedRecipes
    }
}






