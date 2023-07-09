package com.rockettsttudio.eatsease.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rockettsttudio.eatsease.R

class PasswordFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_paswword, container, false)

        val currentPasswordEditText = view.findViewById<EditText>(R.id.etCurrentPassword)
        val newPasswordEditText = view.findViewById<EditText>(R.id.etNewPassword2)
        val updatePasswordButton = view.findViewById<Button>(R.id.updatePasswordButton)

        updatePasswordButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString()
            val newPassword = newPasswordEditText.text.toString()

            // Get the current user from Firebase Authentication
            val user = FirebaseAuth.getInstance().currentUser

            // Check if the user is signed in with Google
            val isGoogleUser = user?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } ?: false

            if (isGoogleUser) {
                // User is logged in with Google, provide instructions to change password with Google
                showToast("Since you're logged in with Google, please update your password directly through Google account settings.")
                openGoogleAccountSettings()
            } else {
                // Reauthenticate the user with their current password
                val credential = EmailAuthProvider.getCredential(user!!.email!!, currentPassword)
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // User re-authenticated successfully, now change the password
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { passwordUpdateTask ->
                                    if (passwordUpdateTask.isSuccessful) {
                                        // Password updated successfully
                                        showToast("Password updated successfully")
                                    } else {
                                        // Password update failed
                                        showToast("Failed to update password")
                                    }
                                }
                        } else {
                            // User re-authentication failed
                            showToast("Failed to re-authenticate")
                        }
                    }
            }
        }

        return view
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun openGoogleAccountSettings() {
        val googleAccountUri = Uri.parse("https://myaccount.google.com/security")
        val openSettingsIntent = Intent(Intent.ACTION_VIEW, googleAccountUri)
        startActivity(openSettingsIntent)
    }
}
