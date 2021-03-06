package com.raymondliang.myapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import com.raymondliang.myapplication.UserAdapter.UserAdapterOnClickHandler
import com.raymondliang.myapplication.databinding.FragmentUserListBinding
import com.raymondliang.myapplication.utils.viewBinding
import java.util.*

class DoctorsListFragment : Fragment(R.layout.fragment_user_list) {
    private val _defaultViewModelProviderFactory by lazy<ViewModelProvider.Factory> {
        object : AbstractSavedStateViewModelFactory(this@DoctorsListFragment, null) {
            override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                return when (modelClass) {
                    DoctorsViewModel::class.java -> DoctorsViewModel(
                            FirebaseDatabase.getInstance().reference,
                            handle) as T
                    else -> super@DoctorsListFragment.getDefaultViewModelProviderFactory().create(modelClass)
                }
            }

        }
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory = _defaultViewModelProviderFactory

    private val doctorList = ArrayList<DoctorItem>()
    private val doctorsViewModel: DoctorsViewModel by viewModels()
    private val binding: FragmentUserListBinding by viewBinding(FragmentUserListBinding::bind)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.swipeContainer.setOnRefreshListener {
            doctorsViewModel.getDoctors()
        }
        binding.recyclerView.adapter = UserAdapter(doctorList, object : UserAdapterOnClickHandler {
            override fun onClick(doctorItem: DoctorItem) {
                val builder = AlertDialog.Builder(requireActivity())
                if (doctorItem.availability) {
                    val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                        when (which) {
                            DialogInterface.BUTTON_POSITIVE -> {
                                val i = Intent(requireActivity(), VideoCallActivity::class.java)
                                i.putExtra(VideoCallActivity.SESSION_ID, "session")
                                startActivity(i)
                            }
                            DialogInterface.BUTTON_NEGATIVE -> {
                            }
                        }
                    }
                    builder.setMessage("Start a call?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show()
                } else {
                    builder.setMessage("Doctor not available, cannot start a call").setPositiveButton("OK", null).show()
                }
            }
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerView.setHasFixedSize(true)

        doctorsViewModel.state.observe(viewLifecycleOwner) {
            when (it) {
                is DoctorsViewModel.State.GetDoctorsSuccess -> {
                    this.doctorList.clear()
                    this.doctorList.addAll(it.doctors)
                    binding.recyclerView.adapter!!.notifyDataSetChanged()
                }
            }
            binding.swipeContainer.isRefreshing = false
        }
    }
}