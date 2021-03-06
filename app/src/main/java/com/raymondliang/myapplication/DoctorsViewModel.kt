package com.raymondliang.myapplication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.raymondliang.myapplication.Data.Doctor

class DoctorsViewModel(private val databaseReference: DatabaseReference,
                       savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _state = MutableLiveData<State>()
    val state: LiveData<State>
        get() = _state

    init {
        getDoctors()
    }

    fun getDoctors() {
        databaseReference.child("Doctors").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Get user value
                        val doctorList = mutableListOf<DoctorItem>()
                        for (ds in dataSnapshot.children) {
                            val doctor = ds.getValue(Doctor::class.java)
                            if (doctor != null) {
                                doctorList.add(DoctorItem(doctor.name, doctor.availability, null, R.drawable.ic_android))
                            }
                        }
                        _state.value = State.GetDoctorsSuccess(doctorList)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(DoctorsViewModel::class.simpleName, "getUser:onCancelled", databaseError.toException())
                        _state.value = State.GetDoctorsFailed(databaseError.toException())
                    }
                })
    }

    sealed class State {
        class GetDoctorsSuccess(val doctors: List<DoctorItem>) : State()
        class GetDoctorsFailed(val e: Exception) : State()
    }
}