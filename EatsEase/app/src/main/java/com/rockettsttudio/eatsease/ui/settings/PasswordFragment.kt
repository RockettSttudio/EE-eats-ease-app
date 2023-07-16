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
import androidx.navigation.Navigation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.databinding.FragmentPaswwordBinding

class PasswordFragment : Fragment() {
    private var _binding: FragmentPaswwordBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaswwordBinding.inflate(inflater, container, false)
        val view = binding.root

        val currentPasswordEditText = binding.etCurrentPassword
        val newPasswordEditText = binding.etNewPassword2
        val updatePasswordButton = binding.updatePasswordButton

        binding.backFlechaPaswword.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_passwordFragment_to_navigation_Settings)
        }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
