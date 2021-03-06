package com.raymondliang.myapplication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.raymondliang.myapplication.databinding.FragmentProfileBinding
import com.raymondliang.myapplication.utils.viewBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    companion object {
        const val USER_INFO = "user_info"
    }
    private val binding: FragmentProfileBinding by viewBinding(FragmentProfileBinding::bind)
    private val userViewModel: UserViewModel by activityViewModels()
    // TODO: Allow user to edit their information?
//    private val database = FirebaseDatabase.getInstance().reference
//    private val storage = FirebaseStorage.getInstance()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = userViewModel.currentUser
        binding.profileName.text = user.name
        binding.profileEmail.text = user.email
        binding.profilePhone.text = user.phone
        binding.profileLocation.text = user.location
        binding.profileSpecialization.text = user.specialization
        binding.profileLicense.text = user.license
        binding.profileEducation.text = user.education
    }
}