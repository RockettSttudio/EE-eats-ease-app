package com.rockettsttudio.eatsease.ui.Ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.databinding.FragmentIngredientesBinding

class IngredientsFragment : Fragment() {

    private lateinit var checkboxIngredients: CheckBox

    private var _binding: FragmentIngredientesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIngredientesBinding.inflate(inflater, container, false)
        checkboxIngredients = binding.CheckboxIngredients
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}