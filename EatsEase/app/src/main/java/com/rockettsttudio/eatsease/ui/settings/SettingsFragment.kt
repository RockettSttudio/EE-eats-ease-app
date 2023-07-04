package com.rockettsttudio.eatsease.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rockettsttudio.eatsease.Login
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.databinding.FragmentSavedBinding
import com.rockettsttudio.eatsease.databinding.FragmentSettingsBinding
import com.rockettsttudio.eatsease.ui.MainActivity
import com.rockettsttudio.eatsease.ui.saved.SavedAdapter

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivity = activity as MainActivity


        binding.backFlechaSettings.setOnClickListener {
            mainActivity.setTopNavigationVisibility(View.VISIBLE) // Show the top_navigation view
            findNavController().navigateUp()
        }

        binding.linearLayoutSecuritySettings.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        Firebase.auth.signOut()
        val intent = Intent(requireContext(), Login::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}