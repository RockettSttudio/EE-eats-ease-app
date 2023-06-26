package com.rockettsttudio.eatsease.ui.recipes

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
import com.rockettsttudio.eatsease.databinding.FragmentRecipesBinding
import com.rockettsttudio.eatsease.ui.ViewModelFactory
import com.rockettsttudio.eatsease.ui.recipes.RecipeViewModel
import com.rockettsttudio.eatsease.App
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.data.models.Recipe


class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentRecipesBinding.inflate(inflater, container, false)

        val app = requireActivity().application as App
        val viewModelFactory = app.viewModelFactory
        recipeViewModel = ViewModelProvider(this, viewModelFactory).get(RecipeViewModel::class.java)

        recipeAdapter = RecipeAdapter(emptyList(), this) // Initially, no recipes to display

        binding.recipeRecyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeAdapter
        }

        val apiKey = "defe9d5425bf4785b81f35a1827edb2a"
        val number = 10
        binding.actionIngredients.setOnClickListener {
            fetchToAdapter(apiKey, number, "vegan")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (recipeViewModel.randomRecipes.value == null) {
            fetchToAdapter("defe9d5425bf4785b81f35a1827edb2a", 10, "")
        } else {
            recipeAdapter.recipes = recipeViewModel.randomRecipes.value ?: emptyList()
            recipeAdapter.notifyDataSetChanged()
        }
    }

    fun fetchToAdapter(apiKey: String, number: Int, tags: String) {
        recipeViewModel.fetchRandomRecipes(apiKey, number, tags)
        recipeViewModel.randomRecipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes != null && recipes.isNotEmpty()) {
                recipeAdapter.recipes = recipes // Update the adapter's data
                recipeAdapter.notifyDataSetChanged() // Notify the adapter of the data change
            } else {
                // Handle the error or show a message to the user
            }
        }
    }

    fun onItemClick(recipe: Recipe) {
        // Handle item click event
        val bundle = Bundle().apply {
            putString("title", recipe.title)
            putString("image", recipe.image)
            putString("title", recipe.title)
            val summary = HtmlCompat.fromHtml(recipe.summary, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            putString("summary", summary)
            val ingredients = recipe.extendedIngredients.joinToString("\n") { ingredient ->
                "${ingredient.name}: ${ingredient.amount} ${ingredient.unit}"
            }
            putString("ingredients", ingredients)// Pass the clicked recipe to the RecipeDetailsActivity
            putString("instructions", recipe.instructions)
        }
        findNavController().navigate(R.id.action_navigation_recipes_to_recipeDetailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}