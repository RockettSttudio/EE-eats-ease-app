package com.rockettsttudio.eatsease.ui.Ingredients

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.databinding.FragmentIngredientsBinding

class IngredientsFragment : Fragment() {

    private var _binding: FragmentIngredientsBinding? = null
    private lateinit var ingredientAdapter: IngredientAdapter
    private lateinit var selectedIngredients: MutableList<String>

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)

        return binding.root
    }


}