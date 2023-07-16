package com.rockettsttudio.eatsease.ui.recipes

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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rockettsttudio.eatsease.ui.MainActivity
import android.content.SharedPreferences
import android.util.Log
import android.widget.RatingBar


class RecipeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var favoritesRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recipeTitle: String
    private lateinit var imgUrl: String
    private lateinit var sourceURL: String
    private lateinit var recipeSummary: String
    private lateinit var recipeIngredients: String
    private lateinit var recipeInstr: String
    private lateinit var ratingBar: RatingBar


    private var currentRating: Float = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFirebase()
        setupArguments()
        setupUI()
        setupRatingBar()
        setupOnBackPressed()
        setupFavoriteButton()
        setupShareButton()
        setupCalendarButton()
    }

    private fun setupFirebase() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        currentUser?.let {
            val database = FirebaseDatabase.getInstance()
            favoritesRef = database.reference.child("users").child(it.uid).child("favorites")
        }
    }

    private fun setupArguments() {
        val arguments = requireArguments()
        recipeTitle = arguments.getString("title").orEmpty()
        imgUrl = arguments.getString("image").orEmpty()
        sourceURL = arguments.getString("sourceURL").orEmpty()
        recipeSummary = arguments.getString("summary").orEmpty()
        recipeIngredients = arguments.getString("ingredients").orEmpty()
        recipeInstr = arguments.getString("instructions").orEmpty()
    }

    private fun setupUI() {
        binding.titleDetailView.text = recipeTitle
        Glide.with(requireContext()).load(imgUrl).into(binding.imageDetailsView)
        binding.descDetailsView.text = recipeSummary
        binding.ingredientsDetailsView.text = recipeIngredients
        binding.instructionsDetailsView.text = recipeInstr
    }

    private fun setupRatingBar() {
        ratingBar = binding.ratingBar
        sharedPreferences = requireContext().getSharedPreferences("RecipeRatings", Context.MODE_PRIVATE)
        currentRating = sharedPreferences.getFloat(recipeTitle, 0.0f)
        ratingBar.rating = currentRating

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            currentRating = rating
            val editor = sharedPreferences.edit()
            editor.putFloat(recipeTitle, currentRating)
            editor.apply()
        }
    }

    private fun setupOnBackPressed() {
        val mainActivity = activity as MainActivity
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
        binding.backFlechaReceta.setOnClickListener {
            mainActivity.setTopNavigationVisibility(View.VISIBLE)
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun setupFavoriteButton() {
        favoritesRef.orderByChild("recipeTitle").equalTo(recipeTitle)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isFavorite = snapshot.exists()
                    binding.favoriteButton.isChecked = isFavorite
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                }
            })

        binding.favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                addToFavorite()
            } else {
                removeFromFavorites()
            }
        }
    }

    private fun setupShareButton() {
        binding.shareRecipe.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareText = "¡Mira esta increíble receta! $sourceURL"
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(shareIntent, "Compartir receta"))
        }
    }

    private fun setupCalendarButton() {
        binding.addCalendar.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, recipeTitle)
            startActivity(intent)
        }
    }

    private fun addToFavorite() {
        val currentUser = auth.currentUser
        currentUser?.let {
            favoritesRef.orderByChild("sourceURL")
                .equalTo(sourceURL)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.d("SavedFragment", "Recipe already added to favorites")
                        } else {
                            val favoriteEntry = favoritesRef.push()
                            val favoriteData = hashMapOf(
                                "recipeTitle" to recipeTitle,
                                "sourceURL" to sourceURL,
                                "recipeSummary" to recipeSummary,
                                "recipeIngredients" to recipeIngredients,
                                "recipeInstructions" to recipeInstr,
                                "imgUrl" to imgUrl
                            )
                            favoriteEntry.setValue(favoriteData)
                                .addOnFailureListener {
                                    Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }
                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(context, "Database error", Toast.LENGTH_SHORT).show()
                    }
                })
        } ?: run {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFromFavorites() {
        val currentUser = auth.currentUser
        currentUser?.let {
            favoritesRef.orderByChild("recipeTitle")
                .equalTo(recipeTitle)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (favoriteSnapshot in dataSnapshot.children) {
                                favoriteSnapshot.ref.removeValue()
                            }
                        } else {
                            Toast.makeText(context, "Favorite not found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(context, "Database error", Toast.LENGTH_SHORT).show()
                    }
                })
        } ?: run {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}

