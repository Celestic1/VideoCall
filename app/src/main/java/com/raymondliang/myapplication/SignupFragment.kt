package com.raymondliang.myapplication

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.raymondliang.myapplication.Data.User
import com.raymondliang.myapplication.databinding.FragmentSignupBinding
import com.raymondliang.myapplication.utils.MySingleton
import org.json.JSONException
import org.json.JSONObject

class SignupFragment : Fragment(R.layout.fragment_signup) {
    private val url = "http://ip-api.com/json"
    private var mAuth: FirebaseAuth? = null
    private var location: String? = null
    // TODO: Unset this in onDestroyView or else memory leak
    private lateinit var binding: FragmentSignupBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignupBinding.bind(view)
        binding.buttonSignup.setOnClickListener {
            signup()
        }
        binding.goToLogin.setOnClickListener {
            toLogin()
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response: JSONObject ->
            try {
                val state = response.getString("regionName")
                val country = response.getString("countryCode")
                location = "$state, $country"
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error: VolleyError -> error.printStackTrace() })
        MySingleton.getInstance(requireActivity()).addToRequestQueue(jsonObjectRequest)
    }

    fun signup() {
        mAuth = FirebaseAuth.getInstance()
        val email = binding.regEmail.text.toString()
        val password = binding.regPassword.text.toString()
        if (!validate()) {
            return
        }
        mAuth!!.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful()) {
                        // Sign in success
                        postUserData()
                        Toast.makeText(this@SignupFragment.requireActivity(), "Registration Successful", Toast.LENGTH_LONG).show()
                        toLogin()
                    } else {
                        Toast.makeText(this@SignupFragment.requireActivity(), "User Authentication Failed: " + (task.exception?.message
                                ?: "Unknown"), Toast.LENGTH_SHORT).show()
                    }
                }
    }

    private fun postUserData() {
        val mDatabase = FirebaseDatabase.getInstance().reference
        val user = User(binding.regName.text.toString(), binding.regEmail.text.toString(), binding.regPhone.text.toString(), binding.regAddress.text.toString(), location, mAuth!!.uid, false)
        mDatabase.child("Users").child(mAuth!!.currentUser!!.uid).setValue(user)
    }

    private fun validate(): Boolean {
        var valid = true
        if (TextUtils.isEmpty(binding.regEmail.text.toString())) {
            binding.regEmail.error = "Required."
            valid = false
        } else {
            binding.regEmail.error = null
        }
        if (TextUtils.isEmpty(binding.regPassword.text.toString())) {
            binding.regPassword.error = "Required."
            valid = false
        } else {
            binding.regPassword.error = null
        }
        if (TextUtils.isEmpty(binding.regName.text.toString())) {
            binding.regName.error = "Required."
            valid = false
        } else {
            binding.regName.error = null
        }
        if (TextUtils.isEmpty(binding.regPhone.text.toString())) {
            binding.regPhone.error = "Required."
            valid = false
        } else {
            binding.regPhone.error = null
        }
        if (TextUtils.isEmpty(binding.regAddress.text.toString())) {
            binding.regAddress.error = "Required."
            valid = false
        } else {
            binding.regAddress.error = null
        }
        return valid
    }

    private fun toLogin() {
        findNavController().popBackStack()
        findNavController().navigate(R.id.loginFragment)
    }
}