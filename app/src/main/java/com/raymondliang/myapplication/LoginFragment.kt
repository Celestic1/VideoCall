package com.raymondliang.myapplication

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.raymondliang.myapplication.databinding.FragmentLoginBinding
import com.raymondliang.myapplication.utils.viewBinding
import java.util.*

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var loadingBar: ProgressDialog? = null
    private val binding: FragmentLoginBinding by viewBinding(FragmentLoginBinding::bind)
    private val userViewModel: UserViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newAccountLink.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
        loadingBar = ProgressDialog(requireActivity())
        binding.test.setOnClickListener {
            login("test1@gmail.com", "123456", false)
        }
        binding.buttonLogin.setOnClickListener {
            login(binding.tvEmail.text.toString(), binding.tvPassword.text.toString())
        }
        binding.newAccountLink.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.signupFragment)
        }

        userViewModel.userState.observe(viewLifecycleOwner) {
            when (it) {
                is UserViewModel.State.UserLoggedIn -> {
                    findNavController().popBackStack()
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.welcomeFragment)
                    Toast.makeText(this@LoginFragment.requireActivity(), "Logged in", Toast.LENGTH_SHORT).show()
                    loadingBar!!.dismiss()
                }
                is UserViewModel.State.UserLoginFailed -> {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this@LoginFragment.requireActivity(), "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    loadingBar!!.dismiss()
                }
            }
        }
    }

    fun login(email: String, password: String, needsValidation: Boolean = true) {
        if (needsValidation && !isValidText()) {
            return
        }
        loadingBar!!.setMessage("Please wait...")
        loadingBar!!.setCanceledOnTouchOutside(true)
        loadingBar!!.show()
        userViewModel.login(email, password)
    }

    private fun isValidText(): Boolean {
        var valid = true
        val email = binding.tvEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.tvEmail.error = "Required."
            valid = false
        } else {
            binding.tvEmail.error = null
        }
        val password = binding.newAccountLink.text.toString()
        if (TextUtils.isEmpty(password)) {
            binding.newAccountLink.error = "Required."
            valid = false
        } else {
            binding.newAccountLink.error = null
        }
        return valid
    }
}