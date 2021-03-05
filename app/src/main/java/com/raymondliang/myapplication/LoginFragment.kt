package com.raymondliang.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.raymondliang.myapplication.databinding.FragmentLoginBinding
import java.util.*

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var mAuth: FirebaseAuth? = null
    private var email: String? = null
    private var password: String? = null
    private val map: MutableMap<String, Any> = HashMap()
    private var database: DatabaseReference? = null
    private var loadingBar: ProgressDialog? = null
    // TODO: Unset this in onDestroyView or else memory leak
    private lateinit var binding: FragmentLoginBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)
        database = FirebaseDatabase.getInstance().reference
        if (savedInstanceState != null) {
            email = savedInstanceState.getString("UserEmail")
            password = savedInstanceState.getString("UserPassword")
        }
        binding.newAccountLink.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.signupFragment)
        }
        loadingBar = ProgressDialog(requireActivity())
        binding.test.setOnClickListener {
            testLogin()
        }
        binding.buttonLogin.setOnClickListener {
            login()
        }
    }

    fun login() {
        mAuth = FirebaseAuth.getInstance()
        email = binding.tvEmail!!.text.toString()
        password = binding.newAccountLink.text.toString()
        if (!validate()) {
            return
        }
        mAuth = FirebaseAuth.getInstance()
        loadingBar!!.setMessage("Please wait...")
        loadingBar!!.setCanceledOnTouchOutside(true)
        loadingBar!!.show()
        mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                            val currentUserId = mAuth!!.currentUser!!.uid
                            val deviceToken = instanceIdResult.token
                            map["device_token"] = deviceToken
                            database!!.child("Users").child(currentUserId).updateChildren(map).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    findNavController().popBackStack()
                                    findNavController().popBackStack()
                                    findNavController().navigate(R.id.welcomeFragment)
                                    Toast.makeText(this@LoginFragment.requireActivity(), "Logged in", Toast.LENGTH_SHORT).show()
                                    loadingBar!!.dismiss()
                                }
                            }
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this@LoginFragment.requireActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        loadingBar!!.dismiss()
                    }
                }
    }

    private fun validate(): Boolean {
        var valid = true
        val email = binding.tvEmail!!.text.toString()
        if (TextUtils.isEmpty(email)) {
            binding.tvEmail!!.error = "Required."
            valid = false
        } else {
            binding.tvEmail!!.error = null
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("UserEmail", binding.tvEmail.text.toString())
        outState.putString("UserPassword", binding.newAccountLink.text.toString())
        super.onSaveInstanceState(outState)
    }

    fun testLogin() {
        mAuth = FirebaseAuth.getInstance()
        email = "test1@gmail.com"
        password = "123456"
        loadingBar!!.setMessage("Please wait...")
        loadingBar!!.setCanceledOnTouchOutside(true)
        loadingBar!!.show()
        mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                            val currentUserId = mAuth!!.currentUser!!.uid
                            val deviceToken = instanceIdResult.token
                            map["device_token"] = deviceToken
                            database!!.child("Users").child(currentUserId).updateChildren(map).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    findNavController().popBackStack()
                                    findNavController().popBackStack()
                                    findNavController().navigate(R.id.welcomeFragment)
                                    Toast.makeText(this@LoginFragment.requireActivity(), "Logged in", Toast.LENGTH_SHORT).show()
                                    loadingBar!!.dismiss()
                                }
                            }
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this@LoginFragment.requireActivity(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }
}