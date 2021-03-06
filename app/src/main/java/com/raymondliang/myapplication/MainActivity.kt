package com.raymondliang.myapplication

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.raymondliang.myapplication.databinding.ActivityMainBinding
import com.raymondliang.myapplication.utils.viewBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    companion object {
        private const val DEFAULT_SHARED_PREFERENCES_NAME = "default_shared_preferences"
    }
    private val _defaultViewModelProviderFactory by lazy<ViewModelProvider.Factory> {
        object : AbstractSavedStateViewModelFactory(this@MainActivity, null) {
            override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
                return when (modelClass) {
                    UserViewModel::class.java -> UserViewModel(
                            getSharedPreferences(DEFAULT_SHARED_PREFERENCES_NAME, MODE_PRIVATE),
                            FirebaseAuth.getInstance(),
                            FirebaseDatabase.getInstance().reference,
                            handle) as T
                    else -> super@MainActivity.getDefaultViewModelProviderFactory().create(modelClass)
                }
            }

        }
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory = _defaultViewModelProviderFactory

    private val binding: ActivityMainBinding by viewBinding(ActivityMainBinding::bind, R.id.main_root)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}