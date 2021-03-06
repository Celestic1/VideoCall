package com.raymondliang.myapplication.utils

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object NetworkClient {
    private var requestQueue: RequestQueue? = null
    @Synchronized
    fun init(applicationContext: Context) {
        if (requestQueue != null) throw IllegalStateException("Can't initialize twice!")
        requestQueue = Volley.newRequestQueue(applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>?) {
        requestQueue!!.add(req)
    }
}