package com.raymondliang.myapplication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.raymondliang.myapplication.databinding.FragmentLoginSignupBinding

class LoginSignupFragment : Fragment(R.layout.fragment_login_signup) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginSignupBinding.bind(view)
        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        binding.registrationLink.setOnClickListener {
            findNavController().navigate(R.id.signupFragment)
        }
    }
}