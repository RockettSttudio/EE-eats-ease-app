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
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.ui.MainActivity
import android.content.SharedPreferences
import android.widget.RatingBar


class RecipeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private var onBackPressedCallback: OnBackPressedCallback? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var favoritesRef: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var ratingBar: RatingBar
    private var currentRating: Float = 0.0f


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Set up Firebase Authentication
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        val database = FirebaseDatabase.getInstance()
        favoritesRef = database.reference.child("users").child(currentUser.uid).child("favorites")

        binding = FragmentRecipeDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mainActivity = activity as MainActivity

        binding.backFlechaReceta.setOnClickListener {
            mainActivity.setTopNavigationVisibility(View.VISIBLE) // Show the top_navigation view
            findNavController().navigateUp()
        }

        val recipeTitle = arguments?.getString("title") // Titulo
        binding.titleDetailView.text = arguments?.getString("title")
        val imgUrl = arguments?.getString("image") // IMAGEN
        val sourceURL = arguments?.getString("sourceURL") // LINK
        Glide.with(requireContext()).load(imgUrl).into(binding.imageDetailsView)
        val recipeSummary = arguments?.getString("summary") // descripcion
        binding.descDetailsView.text = arguments?.getString("summary")
        val recipeIngredients = arguments?.getString("ingredients")
        binding.ingredientsDetailsView.text = arguments?.getString("ingredients")
        val recipeInstr = arguments?.getString("instructions") // instrucciones
        binding.instructionsDetailsView.text = arguments?.getString("instructions")

        // Check if the recipe is already in the user's favorites
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

        binding.addCalendar.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, binding.titleDetailView.text.toString())
            startActivity(intent)
        }

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

        binding.favoriteButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Checkbox is checked, add to favorites
                if (recipeTitle != null && sourceURL != null && recipeSummary != null &&
                    recipeIngredients != null && recipeInstr != null && imgUrl != null
                ) {
                    addToFavorite(
                        requireContext(),
                        recipeTitle,
                        sourceURL,
                        recipeSummary,
                        recipeIngredients,
                        recipeInstr,
                        imgUrl
                    )
                } else {
                    // Handle the case where any of the required arguments is null
                    Toast.makeText(context, "Missing recipe details", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Checkbox is unchecked, remove from favorites
                if (recipeTitle != null) {
                    removeFromFavorites(requireContext(), recipeTitle)
                } else {
                    // Handle the case where favoriteId is null
                    Toast.makeText(context, "Missing favorite ID", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun addToFavorite(
        context: Context,
        recipeTitle: String,
        sourceURL: String,
        recipeSummary: String,
        recipeIngredients: String,
        recipeInstr: String,
        imgUrl: String
    ) {
        // Check if the user is authenticated
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Get a reference to the Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val favoritesRef = database.reference
                .child("users")
                .child(currentUser.uid)
                .child("favorites")

            // Check if the favorite already exists
            favoritesRef.orderByChild("sourceURL")
                .equalTo(sourceURL)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Favorite already exists, show a message or handle it accordingly
                            Toast.makeText(context, "Already added to favorites", Toast.LENGTH_SHORT).show()
                        } else {
                            // Create a new entry in the "favorites" node
                            val favoriteEntry = favoritesRef.push()
                            val favoriteId = favoriteEntry.key

                            // Create a map of the data you want to store
                            val favoriteData = hashMapOf(
                                "recipeTitle" to recipeTitle,
                                "sourceURL" to sourceURL,
                                "recipeSummary" to recipeSummary,
                                "recipeIngredients" to recipeIngredients,
                                "recipeInstructions" to recipeInstr,
                                "imgUrl" to imgUrl
                            )

                            // Set the data for the new favorite entry
                            favoriteEntry.setValue(favoriteData)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    // Handle the failure case
                                    Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle the database error
                        Toast.makeText(context, "Database error", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            // User is not authenticated, show a message or handle it accordingly
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFromFavorites(context: Context, recipeTitle: String) {
        // Check if the user is authenticated
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Get a reference to the Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val favoritesRef = database.reference
                .child("users")
                .child(currentUser.uid)
                .child("favorites")

            // Query the database to find the favorite to remove
            favoritesRef.orderByChild("recipeTitle")
                .equalTo(recipeTitle)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Loop through the matching favorites and remove them
                            for (favoriteSnapshot in dataSnapshot.children) {
                                favoriteSnapshot.ref.removeValue()
                            }
                            Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                        } else {
                            // Favorite not found, show a message or handle it accordingly
                            Toast.makeText(context, "Favorite not found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle the database error
                        Toast.makeText(context, "Database error", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            // User is not authenticated, show a message or handle it accordingly
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
