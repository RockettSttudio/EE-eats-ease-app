package com.rockettsttudio.eatsease.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.navigation.Navigation
import androidx.navigation.findNavController
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

        val isTablet = resources.getBoolean(R.bool.isTablet)
        val navController = requireActivity().findNavController(R.id.nav_host_fragment_activity_main)


        binding.backFlechaSettings.setOnClickListener {
            if (isTablet) {
                val navController2 = requireActivity().findNavController(R.id.nav_host_fragment_activity_main2)
                mainActivity.setTopNavigationVisibility(View.VISIBLE)
                mainActivity.setBottomNavigationVisibility(View.VISIBLE)
                navController.navigateUp()
                navController2.navigateUp()
            }else{
                mainActivity.setTopNavigationVisibility(View.VISIBLE)
                mainActivity.setBottomNavigationVisibility(View.VISIBLE)
                navController.navigateUp()
            }
        }
        binding.LogoutButton.setOnClickListener {
            navigateToLogin()
        }
        binding.linearLayoutHELPCENTERSettings.setOnClickListener{
            helpCenter()
        }

        binding.linearLayoutSecuritySettings.setOnClickListener {
            val navController = Navigation.findNavController(requireView())
            navController.navigate(R.id.action_navigation_Settings_to_passwordFragment)
        }

    }

    private fun navigateToLogin() {
        Firebase.auth.signOut()
        val intent = Intent(requireContext(), Login::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
    private fun helpCenter() {
        val popupMenu = PopupMenu(requireContext(), binding.linearLayoutHELPCENTERSettings, GravityCompat.END)
        popupMenu.menuInflater.inflate(R.menu.help_center_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_faq -> {
                    showFAQFragment()
                    true
                }
                R.id.menu_item_contact -> {
                    sendURL()
                    true
                }
                R.id.menu_item_correo -> {
                    sendEmail()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun sendEmail() {
        val recipientEmail = "suppor1eatsease@gmail.com"
        val subject = "Consulta"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipientEmail))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)

        try {
            startActivity(Intent.createChooser(intent, "Enviar correo electrónico"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No se encontró ninguna aplicación de correo electrónico.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendURL() {
        val url = "http://eatsease.me/Home"

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), "No se encontró ninguna aplicación compatible para abrir el enlace.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showFAQFragment() {
        val faqFragment = FAQFragment()
        faqFragment.show(parentFragmentManager, "faq_dialog")
    }
}