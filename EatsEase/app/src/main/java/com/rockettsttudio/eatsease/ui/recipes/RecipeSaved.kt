package com.rockettsttudio.eatsease.ui.recipes

import android.app.FragmentManagerNonConfig
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rockettsttudio.eatsease.databinding.FragmentIngredientsBinding
import com.rockettsttudio.eatsease.databinding.FragmentSavedBinding

class RecipeSaved : Fragment()  {
    private var _binding: FragmentSavedBinding? =null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)

        return binding.root
    }

}