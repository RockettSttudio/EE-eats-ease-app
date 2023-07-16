package com.rockettsttudio.eatsease

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rockettsttudio.eatsease.ui.MainActivity

class Login : AppCompatActivity() {

    // Declare the necessary views and variables
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var googleLoginButton: SignInButton
    private lateinit var registerButton: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set up Google Sign-In
        setupGoogleSignIn()

        // Set up Firebase Authentication
        auth = Firebase.auth

        bindViews() // Initialize the views
        setListeners() // Set click listeners for buttons
    }

    // Set up Google Sign-In by creating GoogleSignInOptions and GoogleSignInClient
    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        checkCurrentUser() // Check if a user is already signed in
    }

    // Check if a user is already signed in, and update the UI accordingly
    private fun checkCurrentUser() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
    }

    // Set click listeners for buttons
    private fun setListeners() {
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
                showErrorMessage("Please enter email and password")
            } else {
                signIn(email, password)
            }
        }

        registerButton.setOnClickListener {
            startRegisterActivity()
        }

        googleLoginButton.setOnClickListener {
            signInGoogle()
        }
    }

    // Start the Google sign-in flow
    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    // Handle the result of the Google sign-in flow
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    // Handle the result of the Google sign-in task
    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            updateUI(null, account)
                        } else {
                            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        } else {
            Toast.makeText(this, task.exception?.message, Toast.LENGTH_SHORT).show()
        }
    }

    // Sign in with email and password
    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    showErrorMessage("Authentication failed.")
                    updateUI(null)
                }
            }
    }

    // Initialize the views by finding them in the layout
    private fun bindViews() {
        emailEditText = findViewById(R.id.username_login)
        passwordEditText = findViewById(R.id.password_login)
        loginButton = findViewById(R.id.login_button)
        registerButton = findViewById(R.id.login_to_register_button)
        googleLoginButton = findViewById(R.id.sing_in_with_google)
    }

    // Update the UI based on the user's sign-in status
    private fun updateUI(user: FirebaseUser?, account: GoogleSignInAccount? = null) {
        if (user != null || account != null) {
            navigateToMain() // If signed in, navigate to the main activity
        } else {
            showErrorMessage("Sign-in failed. Please check your credentials.")
            clearInputFields() // If sign-in failed, clear the input fields and focus on the email field
            focusOnEmailField()
        }
    }

    // Navigate to the main activity
    private fun navigateToMain() {
        val intent = Intent(this@Login, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Start the register activity
    private fun startRegisterActivity() {
        val intent = Intent(this@Login, Register::class.java)
        startActivity(intent)
    }

    // Show an error message in a Toast
    private fun showErrorMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Clear the input fields
    private fun clearInputFields() {
        emailEditText.text.clear()
        passwordEditText.text.clear()
    }

    // Focus on the email field
    private fun focusOnEmailField() {
        emailEditText.requestFocus()
    }
}

