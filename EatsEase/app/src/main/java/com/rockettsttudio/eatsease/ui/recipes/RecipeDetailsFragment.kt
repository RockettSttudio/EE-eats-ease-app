package com.rockettsttudio.eatsease.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rockettsttudio.eatsease.databinding.FragmentRecipeDetailsBinding

class RecipeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backFlechaReceta.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.titleDetailView.text = arguments?.getString("title")
        val imgUrl = arguments?.getString("image")
        Glide.with(requireContext()).load(imgUrl).into(binding.imageDetailsView)
        binding.descDetailsView.text = arguments?.getString("summary")
        binding.ingredientsDetailsView.text = arguments?.getString("ingredients")
        binding.instructionsDetailsView.text = arguments?.getString("instructions")

        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback as OnBackPressedCallback
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
    }

}