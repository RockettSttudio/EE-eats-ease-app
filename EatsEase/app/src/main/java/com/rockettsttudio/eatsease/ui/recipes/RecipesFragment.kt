package com.rockettsttudio.eatsease.ui.recipes

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.core.text.HtmlCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.rockettsttudio.eatsease.databinding.FragmentRecipesBinding
import com.rockettsttudio.eatsease.ui.ViewModelFactory
import com.rockettsttudio.eatsease.ui.recipes.RecipeViewModel
import com.rockettsttudio.eatsease.App
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.data.models.Recipe
import com.rockettsttudio.eatsease.ui.MainActivity


class RecipesFragment : Fragment() {
    private val selectedIngredients = mutableListOf<String>()
    private var _binding: FragmentRecipesBinding? = null
    private lateinit var recipeViewModel: RecipeViewModel
    private lateinit var recipeRecyclerView: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private var originalRecipes: List<Recipe> = emptyList()

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (recipeViewModel.randomRecipes.value == null) {
            fetchToAdapter("defe9d5425bf4785b81f35a1827edb2a", 40, "")
        } else {
            recipeAdapter.recipes = recipeViewModel.randomRecipes.value ?: emptyList()
            recipeAdapter.notifyDataSetChanged()
        }
        val apikey_roque ="00b02f1c12194c83ac59aa715644f859"
        val apikey_moran ="74f6c26b27ae445c80b6c726383271c6"
        val apiKey_moran_2 = "ccbf268bebb744bd915a50dd48e0d88c"
        val apiKey = "defe9d5425bf4785b81f35a1827edb2a"
        val number = 40
        binding.asianCuisineCardView.setOnClickListener {
            recipeViewModel.setRecipes()
            fetchToAdapter(apiKey, number, "asian")
        }
        binding.veganCuisineCardView.setOnClickListener {
            recipeViewModel.setRecipes()
            fetchToAdapter(apiKey, number, "vegan,vegetarian")
        }
        binding.mexicanCuisineCardView.setOnClickListener {
            recipeViewModel.setRecipes()
            fetchToAdapter(apiKey_moran_2, number, "mexican")
        }
        binding.dessertCardView.setOnClickListener {
            recipeViewModel.setRecipes()
            fetchToAdapter(apikey_moran, number, "dessert")
        }
        binding.drinksCardView.setOnClickListener {
            recipeViewModel.setRecipes()
            fetchToAdapter(apiKey, number, "drink")
        }
        binding.italianCuisineCardView.setOnClickListener {
            recipeViewModel.setRecipes()
            fetchToAdapter(apiKey, number, "italian")
        }
        binding.randomCardView.setOnClickListener {
            recipeViewModel.setRecipes()
            fetchToAdapter(apiKey, number, "")
        }
        binding.snackCardView.setOnClickListener {
            recipeViewModel.setRecipes()
            fetchToAdapter(apiKey, number, "snacks")
        }
        binding.lunchCardView.setOnClickListener {
            recipeViewModel.setRecipes()
            fetchToAdapter(apiKey, number, "lunch")
        }
        //  SearchRecipe two
        binding.searchRecipesRecipes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                filterRecipes(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                filterRecipes(newText)
                return true
            }
        })
        binding.actionIngredients.setOnClickListener {
            val availableIngredients = listOf("Tomato", "Onion", "Bean", "chocolate", "milk", "sugar", "vanilla", "eggs", "bread", "coffee", "cream",
                "juice", "salt", "mangoes", "oil", "cheese", "chicken", "garlic", "water", "jalapeño", "orange", "pineapple", "lemon", "coconut", "ice"
            ) // Agrega aquí los ingredientes

            val checkedItems = BooleanArray(availableIngredients.size) { selectedIngredients.contains(availableIngredients[it]) }

            val inflater = LayoutInflater.from(requireContext())
            val dialogView = inflater.inflate(R.layout.dialog_select_ingredients, null)
            val listView = dialogView.findViewById<ListView>(R.id.listIngredients)

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_multiple_choice, availableIngredients)
            listView.adapter = adapter
            listView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            for ((index, ingredient) in availableIngredients.withIndex()) {
                if (selectedIngredients.contains(ingredient)) {
                    listView.setItemChecked(index, true)
                }
            }

            val builder = AlertDialog.Builder(requireContext())
            builder.setView(dialogView)
            builder.setPositiveButton("Apply") { _, _ ->
                val selectedPositions = listView.checkedItemPositions
                val selectedIngredients = mutableListOf<String>()
                for (position in 0 until selectedPositions.size()) {
                    if (selectedPositions.valueAt(position)) {
                        val ingredient = availableIngredients[selectedPositions.keyAt(position)]
                        selectedIngredients.add(ingredient)
                    }
                }
                filterRecipesByIngredient(selectedIngredients)
            }
            builder.setNegativeButton("Cancel", null)

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun filterRecipesByIngredient(ingredients: List<String>) {
        val filteredRecipes = originalRecipes.filter { recipe ->
            ingredients.all { ingredient ->
                recipe.extendedIngredients.any { extendedIngredient ->
                    extendedIngredient.name.contains(ingredient, ignoreCase = true)
                }
            }
        }

        recipeAdapter.recipes = filteredRecipes
        recipeAdapter.notifyDataSetChanged()
    }





    private fun fetchToAdapter(apiKey: String, number: Int, tags: String) {
        recipeViewModel.fetchRandomRecipes(apiKey, number, tags)
        recipeViewModel.randomRecipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes != null && recipes.isNotEmpty()) {
                originalRecipes = recipes // Guarda la lista original de recetas
                recipeAdapter.recipes = recipes // Actualiza adapter data
                recipeAdapter.notifyDataSetChanged() // Notifica de cambios en la data
            } else {
                // Handle the error or show a message to the user
            }
        }
    }
    private fun filterRecipes(query: String) {
        val filteredRecipes = if (query.isNotBlank()) {
            originalRecipes.filter { recipe ->
                recipe.title.contains(query, ignoreCase = true)
            }
        } else {
            originalRecipes
        }
        recipeAdapter.recipes = filteredRecipes // Actualiza los datos del adaptador con las recetas filtradas
        recipeAdapter.notifyDataSetChanged() // Notifica de cambios en la data
    }

    fun onItemClick(recipe: Recipe) {
        // Handle item click event
        val mainActivity = activity as MainActivity
        val isTablet = resources.getBoolean(R.bool.isTablet)
        val navController = findNavController()

        val bundle = Bundle().apply {
            putString("title", recipe.title)
            putString("image", recipe.image)
            putString("title", recipe.title)
            val summary = HtmlCompat.fromHtml(recipe.summary, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            putString("summary", summary)
            putString("sourceURL", recipe.sourceUrl)
            val ingredients = recipe.extendedIngredients.joinToString("\n") { ingredient ->
                "${ingredient.name}: ${ingredient.amount} ${ingredient.unit}"
            }
            putString("ingredients", ingredients)// Pass the clicked recipe to the RecipeDetailsActivity
            putString("instructions", recipe.instructions)
        }

        if (isTablet) {
            val navController2 = requireActivity().findNavController(R.id.nav_host_fragment_activity_main2)
            mainActivity.setTopNavigationVisibility(View.VISIBLE)
            navController2.navigate(R.id.recipeDetailsFragment, bundle)
        } else {
            mainActivity.setTopNavigationVisibility(View.GONE)
            navController.navigate(R.id.action_navigation_recipes_to_recipeDetailsFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}