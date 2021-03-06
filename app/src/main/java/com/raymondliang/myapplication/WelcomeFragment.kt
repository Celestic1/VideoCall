package com.raymondliang.myapplication

import android.app.ProgressDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import com.raymondliang.myapplication.Data.User
import com.raymondliang.myapplication.databinding.FragmentWelcomeBinding
import com.raymondliang.myapplication.utils.viewBinding
import java.util.*

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {
    companion object {
        private const val TAG = "WelcomeActivity"
    }
    private lateinit var user: User
    private lateinit var database: DatabaseReference
    private var loadingBar: ProgressDialog? = null
    private val binding: FragmentWelcomeBinding by viewBinding(FragmentWelcomeBinding::bind)
    private val userViewModel: UserViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        loadingBar = ProgressDialog(requireActivity())
        binding.logoutButton.setOnClickListener {
            logout()
        }
        binding.tvProfile.setOnClickListener {
            profileSettings()
        }
        binding.statusButton.setOnClickListener {
            toggleStatus()
        }
        binding.viewAvailableButton.setOnClickListener {
            viewUserList()
        }

        user = userViewModel.currentUser
        binding.tvWelcome.text = "Welcome ${user.name}"
        changeStatus(user.availability)
        userViewModel.userState.observe(viewLifecycleOwner) {
            when (it) {
                is UserViewModel.State.UserLoggedOut -> {
                    Toast.makeText(this@WelcomeFragment.requireActivity(), "Successfully logged out.", Toast.LENGTH_SHORT).show()
                    loadingBar!!.dismiss()
                    findNavController().popBackStack()
                    findNavController().navigate(R.id.loginSignupFragment)
                }
            }
        }
    }

    private fun profileSettings() {
        findNavController().navigate(R.id.profileFragment)
    }

    private fun logout() {
        loadingBar!!.setMessage("Logging Out")
        loadingBar!!.setCanceledOnTouchOutside(true)
        loadingBar!!.show()
        userViewModel.logout()
    }

    private fun viewUserList() {
        findNavController().navigate(R.id.userListFragment)
    }

    private fun changeStatus(available: Boolean) {
        binding.statusText.text = if (available) getString(R.string.currently_online) else getString(R.string.currently_offline)
        binding.statusText.setTextColor(if (available) Color.GREEN else Color.RED)

    }

    private fun toggleStatus() {
        Log.d(TAG, "status button clicked")
        userViewModel.toggleAvailability()
        changeStatus(user.availability)
        Toast.makeText(requireActivity(), "Online status changed.", Toast.LENGTH_LONG).show()
    }
}