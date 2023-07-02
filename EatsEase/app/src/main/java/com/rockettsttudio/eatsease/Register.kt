package com.rockettsttudio.eatsease

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.ui.MainActivity

class Register : AppCompatActivity() {

    // UI elements
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var birthdateEditText: EditText
    private lateinit var nameEditText: EditText
    private lateinit var registerButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initializeAuth() // Initialize Firebase authentication
        bindViews() // Bind UI elements to variables
        setListeners() // Set click listener for the register button
    }

    private fun initializeAuth() {
        auth = Firebase.auth // Initialize FirebaseAuth instance
    }

    private fun setListeners() {
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString().trim() // Get email text and remove leading/trailing whitespace
            val password = passwordEditText.text.toString().trim() // Get password text and remove leading/trailing whitespace
            val confirmPassword = confirmPasswordEditText.text.toString().trim() // Get confirm password text and remove leading/trailing whitespace
            val birthdate = birthdateEditText.text.toString().trim() // Get birthdate text and remove leading/trailing whitespace
            val username = nameEditText.text.toString().trim() // Get username text and remove leading/trailing whitespace

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || birthdate.isEmpty() || username.isEmpty()) {
                showToast("Please fill in all fields") // Display error message if any field is empty
            } else if (!isEmailValid(email)) {
                showToast("Please enter a valid email address")
            } else if (!isPasswordSecure(password)) {
                showToast("Please choose a stronger password")
            } else if (password != confirmPassword) {
                showToast("Passwords do not match") // Display error message if passwords do not match
            } else {
                createAccount(email, password) // Create a new account with the provided email and password
            }
        }
    }



    private fun isEmailValid(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun isPasswordSecure(password: String): Boolean {
        // Implement your password strength checking logic here
        return password.length >= 8 &&
                password.matches(".*[a-z].*".toRegex()) &&
                password.matches(".*[A-Z].*".toRegex())
        //&& password.matches(".*\\d.*".toRegex()) && password.matches(".*[@#$%^&+=!].*".toRegex())
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    showToast("Account created") // Show success message
                    updateUI(user) // Account creation successful, update UI with the signed-in user's information
                } else {
                    showToast("Authentication failed.") // Show error message
                    updateUI(null)
                }
            }
    }

    private fun bindViews() {
        emailEditText = findViewById(R.id.email_sign_up) // Find and assign the email EditText
        passwordEditText = findViewById(R.id.password_sign_up) // Find and assign the password EditText
        confirmPasswordEditText = findViewById(R.id.confirm_password_sign_up)
        registerButton = findViewById(R.id.sing_up_button) // Find and assign the register button
        birthdateEditText = findViewById(R.id.birthdate_sign_up) // Find and assign the birthdate EditText
        nameEditText = findViewById(R.id.name_sign_up)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            navigateToMain() // User is signed in, navigate to the main activity
        } else {
            // User is not signed in, handle accordingly
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this@Register, MainActivity::class.java)
        startActivity(intent) // Start the main activity
        finish() // Optional: Call finish() to close the register activity and prevent the user from going back to it
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show() // Show a Toast message
    }

    companion object {
        private const val TAG = "EmailPassword"
    }
}
