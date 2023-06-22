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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkboxIngredients = binding.CheckboxIngredients
        checkboxIngredients.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Acciones cuando el CheckBox está seleccionado
            } else {
                // Acciones cuando el CheckBox no está seleccionado
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
