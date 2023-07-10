package com.rockettsttudio.eatsease.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.data.models.Recipe
import com.rockettsttudio.eatsease.data.models.RecipeFeed
import com.rockettsttudio.eatsease.data.network.services.EastsEaseApiService
import com.rockettsttudio.eatsease.databinding.FragmentHomeBinding
import com.rockettsttudio.eatsease.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeedAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerview_home)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = FeedAdapter(emptyList(), this)
        recyclerView.adapter = adapter

        // Make the API request
        val retrofit = Retrofit.Builder()
            .baseUrl("https://eatsease.me/") // Update with your API URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val recipeApi = retrofit.create(EastsEaseApiService::class.java)
        val call = recipeApi.getPosts()
        call.enqueue(object : Callback<List<RecipeFeed>> {
            override fun onResponse(call: Call<List<RecipeFeed>>, response: Response<List<RecipeFeed>>) {
                if (response.isSuccessful) {
                    val recipes = response.body()
                    recipes?.let {
                        adapter.setRecipes(it)
                    }
                } else {
                    // Handle error
                }
            }

            override fun onFailure(call: Call<List<RecipeFeed>>, t: Throwable) {
                // Handle failure
            }
        })

        return view
    }

    fun onItemClick(recipe: RecipeFeed) {
        // Handle item click event
        val mainActivity = activity as MainActivity
        val isTablet = resources.getBoolean(R.bool.isTablet)
        val navController = findNavController()

        val bundle = Bundle().apply {
            putString("title", recipe.title)
            putString("summary", recipe.description)
            putString("ingredients", recipe.ingredients)// Pass the clicked recipe to the RecipeDetailsActivity
            putString("instructions", recipe.instructions)
        }

        if (isTablet) {
            val navController2 = requireActivity().findNavController(R.id.nav_host_fragment_activity_main2)
            mainActivity.setTopNavigationVisibility(View.VISIBLE)
            navController2.navigate(R.id.recipeDetailsFragment, bundle)
        } else {
            mainActivity.setTopNavigationVisibility(View.GONE)
            navController.navigate(R.id.recipeDetailsFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}