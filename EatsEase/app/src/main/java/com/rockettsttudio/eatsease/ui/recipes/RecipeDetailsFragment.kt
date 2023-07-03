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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class RecipeDetailsFragment : Fragment() {

    private lateinit var binding: FragmentRecipeDetailsBinding
    private var onBackPressedCallback: OnBackPressedCallback? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var favoritesRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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

        binding.backFlechaReceta.setOnClickListener {
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

                            // Set the data in the database under the generated favoriteId
                            favoriteEntry.setValue(favoriteData)
                                .addOnSuccessListener {
                                    // Data successfully added to the database
                                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    // Error occurred while adding data to the database
                                    Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle the error
                        Toast.makeText(context, "Failed to check favorite status", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }



    private fun removeFromFavorites(context: Context, recipeTitle: String) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val database = FirebaseDatabase.getInstance()
            val favoritesRef = database.reference
                .child("users")
                .child(currentUser.uid)
                .child("favorites")

            favoritesRef.orderByChild("recipeTitle")
                .equalTo(recipeTitle)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Recipe found, remove it from favorites
                            val favoriteSnapshot = dataSnapshot.children.first()
                            favoriteSnapshot.ref.removeValue()
                                .addOnSuccessListener {
                                    // Successfully removed from favorites
                                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    // Error occurred while removing from favorites
                                    Toast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Recipe not found in favorites
                            Toast.makeText(context, "Recipe not found in favorites", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle the error
                        Toast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback?.remove()
        onBackPressedCallback = null
    }
}
