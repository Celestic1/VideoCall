package com.raymondliang.myapplication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.raymondliang.myapplication.Data.User
import com.raymondliang.myapplication.databinding.FragmentWelcomeBinding
import java.util.*

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {
    companion object {
        private const val TAG = "WelcomeActivity"
    }
    private var title: TextView? = null
    private var profile: TextView? = null
    private var statusText: TextView? = null
    private var logout: Button? = null
    private var docList: Button? = null
    private var status: Button? = null
    private var uid: String? = null
    private val s: String? = null
    private var mDatabase: DatabaseReference? = null
    private var currUser: User? = null
    private val map: MutableMap<String, Any> = HashMap()
    private val online = false
    private var loadingBar: ProgressDialog? = null
    private lateinit var binding: FragmentWelcomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWelcomeBinding.bind(view)
        mDatabase = FirebaseDatabase.getInstance().reference
        title = requireActivity().findViewById(R.id.tv_welcome)
        profile = requireActivity().findViewById(R.id.tv_profile)
        logout = requireActivity().findViewById(R.id.logout_button)
        docList = requireActivity().findViewById(R.id.viewAvailable_button)
        status = requireActivity().findViewById(R.id.status_button)
        statusText = requireActivity().findViewById(R.id.status_text)
        loadingBar = ProgressDialog(requireActivity())
        binding.statusButton.setOnClickListener {
            //status()
        }
        binding.logoutButton.setOnClickListener {
            logout(view)
        }
        binding.tvProfile.setOnClickListener {
            profileSettings(view)
        }
        binding.viewAvailableButton.setOnClickListener {
            viewUserList(view)
        }
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            uid = user.uid
            retrieveUserInfo(uid!!)
        }
    }

    private fun retrieveUserInfo(userId: String) {
        mDatabase!!.child("Users").child(userId).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Get user value
                        currUser = dataSnapshot.getValue(User::class.java)
                        val msg = "Welcome " + currUser!!.name
                        title!!.text = msg
                        //                        if(!currUser.getAvailability()){
//                            s = "Currently Offline";
//                            statusText.setText(s);
//                            statusText.setTextColor(Color.RED);
//                        } else {
//                            s = "Currently Online";
//                            statusText.setText(s);
//                            statusText.setTextColor(Color.GREEN);
//                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                    }
                })
    }

    fun profileSettings(view: View?) {
        val i = Intent(requireActivity(), ProfileActivity::class.java)
        i.putExtra("User info", currUser)
        requireActivity().startActivity(i)
    }

    fun logout(view: View?) {
        loadingBar!!.setMessage("Logging Out")
        loadingBar!!.setCanceledOnTouchOutside(true)
        loadingBar!!.show()
        if (currUser!!.availability) {
            map["availability"] = false
            mDatabase!!.child("Users").child(uid!!).updateChildren(map)
            mDatabase!!.child("Users").child(uid!!).addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            FirebaseAuth.getInstance().signOut()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException())
                        }
                    })
        }
        map.clear()
        Toast.makeText(this@WelcomeFragment.requireActivity(), "Successfully logged out.", Toast.LENGTH_SHORT).show()
        loadingBar!!.dismiss()
        requireActivity().finish()
    }

    fun viewUserList(view: View?) {
        val i = Intent(requireActivity(), UserListActivity::class.java)
        requireActivity().startActivity(i)
    }
    //    public void changeStatus(View view) {

    //        Log.d(TAG, "status button clicked");
    //
    //        if(!online) {
    //            online = true;
    //            map.put("availability", online);
    //            mDatabase.child("Users").child(uid).updateChildren(map);
    //            map.clear();
    //            mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(
    //                    new ValueEventListener() {
    //                        @Override
    //                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    //                            if(dataSnapshot.child("availability").getValue() == online) {
    //                                s = "Currently Online";
    //                                statusText.setText(s);
    //                                statusText.setTextColor(Color.GREEN);
    //                                Toast.makeText(WelcomeActivity.this, "Online status changed.", Toast.LENGTH_LONG).show();
    //                            }
    //                        }
    //
    //                        @Override
    //                        public void onCancelled(DatabaseError databaseError) {
    //                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
    //                        }
    //                    });
    //        } else {
    //            online = false;
    //            map.put("availability", online);
    //            mDatabase.child("Users").child(uid).updateChildren(map);
    //            map.clear();
    //            mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(
    //                    new ValueEventListener() {
    //                        @Override
    //                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
    //                            if(dataSnapshot.child("availability").getValue() == online) {
    //                                s = "Currently Offline";
    //                                statusText.setText(s);
    //                                statusText.setTextColor(Color.RED);
    //                                Toast.makeText(WelcomeActivity.this, "Online status changed.", Toast.LENGTH_LONG).show();
    //                            }
    //                        }
    //
    //                        @Override
    //                        public void onCancelled(DatabaseError databaseError) {
    //                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
    //                        }
    //                    });
    //        }
    //    }
}