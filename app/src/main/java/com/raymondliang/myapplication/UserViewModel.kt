package com.raymondliang.myapplication

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.installations.FirebaseInstallations
import com.raymondliang.myapplication.Data.User
import com.raymondliang.myapplication.utils.SingleLiveEvent
import org.json.JSONObject

class UserViewModel(private val sharedPreferences: SharedPreferences,
                    private val firebaseAuth: FirebaseAuth,
                    private val database: DatabaseReference,
                    savedStateHandle: SavedStateHandle) : ViewModel() {
    companion object {
        private const val USER = "user"
    }
    private val _userState = SingleLiveEvent<State>()
    val userState: LiveData<State>
        get() = _userState

    private var _currentUser: User? = null
        set(value) {
            if (value == null) {
                sharedPreferences.edit().remove(USER).apply()
            } else {
                sharedPreferences.edit().putString(USER, value.toJson().toString()).apply()
            }
            field = value
        }

    val currentUser: User
        get() = _currentUser!!

    init {
        val json = sharedPreferences.getString(USER, null)
        _currentUser = if (json != null) {
            val user = User.JSON_CREATOR().fromJson(JSONObject(json))
            if (user == null) {
                _userState.value = State.UserLoggedOut()
            } else {
                _userState.value = State.UserLoggedIn(user)
            }
            user
        } else {
            _userState.value = State.UserLoggedOut()
            null
        }
    }

    fun login(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                        FirebaseInstallations.getInstance().id.addOnSuccessListener { instanceIdResult ->
                            val currentUserId = firebaseAuth.currentUser!!.uid
                            val map = mutableMapOf<String, Any>()
                            map["device_token"] = instanceIdResult
                            val currentUserFirebase = database.child("Users").child(currentUserId)
                            currentUserFirebase.updateChildren(map).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    currentUserFirebase.addListenerForSingleValueEvent(
                                            object : ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    // Get user value
                                                    //TODO: Stop relying on Firebase to deserialize this for you, bad practice,
                                                    // relies on variables having the same names as db keys
                                                    _currentUser = dataSnapshot.getValue(User::class.java)
                                                    val currentUser = _currentUser
                                                    if (currentUser != null) {
                                                        _userState.value = State.UserLoggedIn(currentUser)
                                                    } else {
                                                        _userState.value = State.UserLoginFailed()
                                                    }
                                                }

                                                override fun onCancelled(databaseError: DatabaseError) {
                                                    Log.w(UserViewModel::class.simpleName, "getUser:onCancelled", databaseError.toException())
                                                    _userState.value = State.UserLoginFailed(databaseError.toException())
                                                }
                                            })
                                }
                            }
                        }
                    } else {
                        _userState.value = State.UserLoginFailed()
                    }
                }
    }

    fun logout() {
        val currentUser = _currentUser ?: return
        if (currentUser.availability) {
            val map = mutableMapOf<String, Any>()
            map["availability"] = false
            database.child("Users").child(currentUser.getuid()).updateChildren(map)
            database.child("Users").child(currentUser.getuid()).addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            FirebaseAuth.getInstance().signOut()
                            this@UserViewModel._currentUser = null
                            _userState.value = State.UserLoggedOut()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            _userState.value = State.UserLogoutFailed(databaseError.toException())
                            Log.w(UserViewModel::class.simpleName, "getUser:onCancelled", databaseError.toException())
                        }
                    })
        } else {
            this._currentUser = null
            _userState.value = State.UserLoggedOut()
        }
    }

    fun toggleAvailability() {
        val currentUser = _currentUser ?: return
        val map = mutableMapOf<String, Any>()
        currentUser.availability = !currentUser.availability
        map["availability"] = currentUser.availability
        database.child("Users").child(currentUser.getuid()).updateChildren(map)
        this._currentUser = currentUser

    }

    fun hasUser(): Boolean = _currentUser != null

    sealed class State {
        class UserLoggedIn(val user: User) : State()
        class UserLoginFailed(val e: Exception? = null) : State()
        class UserLoggedOut() : State()
        class UserLogoutFailed(val e: Exception? = null) : State()
    }
}