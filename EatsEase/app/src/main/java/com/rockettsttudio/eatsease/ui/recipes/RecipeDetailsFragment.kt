package com.rockettsttudio.eatsease.ui.recipes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rockettsttudio.eatsease.databinding.FragmentRecipeDetailsBinding
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Button
import android.widget.CheckBox

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
        binding.favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Checkbox is checked, perform desired action
            } else {
                // Checkbox is unchecked, perform desired action
            }
        }


        binding.addCalendar.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, binding.titleDetailView.text.toString())
            startActivity(intent)
        }

        val recipeTitle = arguments?.getString("title") //Titulo
        binding.titleDetailView.text = arguments?.getString("title")
        val imgUrl = arguments?.getString("image") //IMAGEN
        val sourceURL = arguments?.getString("sourceURL") //LINK
        Glide.with(requireContext()).load(imgUrl).into(binding.imageDetailsView)
        val recipeSummary = arguments?.getString("summary") //descripcion
        binding.descDetailsView.text = arguments?.getString("summary")
        val recipeIngredients = arguments?.getString("ingredients")
        binding.ingredientsDetailsView.text = arguments?.getString("ingredients")
        val recipeInstr =  arguments?.getString("instructions") //instruciones
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
        binding.shareRecipe.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareText = "¡Mira esta increíble receta! ${arguments?.getString("sourceURL")}"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(shareIntent, "Compartir receta"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
    }

}
