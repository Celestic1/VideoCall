package com.raymondliang.myapplication.utils

import android.view.View
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class FragmentViewBindingProperty<T : ViewBinding>(
        val fragment: Fragment,
        val viewBindingFactory: (View) -> T
) : ReadOnlyProperty<Fragment, T> {
    private var binding: T? = null

    init {
        fragment.lifecycle.addObserver(object : DefaultLifecycleObserver {
            val viewLifecycleOwnerLiveDataObserver =
                    Observer<LifecycleOwner?> {
                        val viewLifecycleOwner = it ?: return@Observer

                        viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                            override fun onDestroy(owner: LifecycleOwner) {
                                binding = null
                            }
                        })
                    }

            override fun onCreate(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.observeForever(viewLifecycleOwnerLiveDataObserver)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                fragment.viewLifecycleOwnerLiveData.removeObserver(viewLifecycleOwnerLiveDataObserver)
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        val binding = binding
        if (binding != null) {
            return binding
        }

        val lifecycle = fragment.viewLifecycleOwner.lifecycle
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            throw IllegalStateException("Should not attempt to get bindings when Fragment views are destroyed.")
        }

        return viewBindingFactory(thisRef.requireView()).also { this.binding = it }
    }
}

inline fun <reified T : ViewBinding> Fragment.viewBinding(noinline viewBinder: (View) -> T): ReadOnlyProperty<Fragment, T> {
    return FragmentViewBindingProperty(this, viewBinder)
}

class ActivityViewBindingProperty<T : ViewBinding>(private val viewBinder: (View) -> T, private val layoutId: Int) : ReadOnlyProperty<FragmentActivity, T> {
    private var binding: T? = null
    @MainThread
    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        val binding = binding
        if (binding != null) {
            return binding
        }

        thisRef.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                owner.lifecycle.removeObserver(this)
                this@ActivityViewBindingProperty.binding = null
            }
        })
        return viewBinder(thisRef.window.decorView.findViewById(layoutId)!!).also { this.binding = it }
    }
}

inline fun <reified T : ViewBinding> FragmentActivity.viewBinding(noinline viewBinder: (View) -> T, layoutId: Int): ReadOnlyProperty<FragmentActivity, T> {
    return ActivityViewBindingProperty(viewBinder, layoutId)
}