package com.rockettsttudio.eatsease.ui.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.data.models.SavedRecipes
import com.rockettsttudio.eatsease.databinding.FragmentSavedBinding
import com.rockettsttudio.eatsease.repositories.SavedRecipeRepository
import com.rockettsttudio.eatsease.ui.MainActivity

class SavedFragment : Fragment(), SavedAdapter.OnItemClickListener {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!


    private lateinit var savedAdapter: SavedAdapter
    private lateinit var recipeRepository: SavedRecipeRepository

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

        recipeRepository = SavedRecipeRepository()

        // Observe the saved recipes from the repository
        recipeRepository.getSavedRecipes().observe(viewLifecycleOwner) { savedRecipes ->
            savedAdapter.setRecipes(savedRecipes)
        }
    }

    override fun onItemClick(recipe: SavedRecipes) {
        // Handle item click event
        val mainActivity = requireActivity() as MainActivity
        val isTablet = resources.getBoolean(R.bool.isTablet)
        val navController = findNavController()

        val bundle = Bundle().apply {
            putString("title", recipe.recipeTitle)
            putString("image", recipe.imgUrl)
            val summary = HtmlCompat.fromHtml(recipe.recipeSummary, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            putString("summary", summary)
            putString("sourceURL", recipe.sourceURL)
            putString("ingredients", recipe.recipeIngredients)
            putString("instructions", recipe.recipeInstructions)
        }

        if (isTablet) {
            val navController2 = requireActivity().findNavController(R.id.nav_host_fragment_activity_main2)
            mainActivity.setTopNavigationVisibility(View.VISIBLE)
            navController2.navigate(R.id.recipeDetailsFragment, bundle)
        } else {
            mainActivity.setTopNavigationVisibility(View.GONE)
            navController.navigate(R.id.action_navigation_saved_to_recipeDetailsFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}







