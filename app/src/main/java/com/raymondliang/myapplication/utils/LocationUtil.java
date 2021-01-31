package com.raymondliang.myapplication.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


public class LocationUtil {

    private static final String TAG = "LocationUtil";
    private static String location;

    public static String getLocation(Context context) {
        if (!isConnectedToNetwork(context))
            Toast.makeText(context, "Error: No internet connection.", Toast.LENGTH_LONG).show();
        else {
            String url = "http://ip-api.com/json";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String state = response.getString("regionName");
                            String country = response.getString("countryCode");
                            location = state + ", " + country;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
            MySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);
        }
        Log.d(TAG, "location: " + location);
        return location;
    }

    public static String getWifiIPAddress(Context context) {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format(Locale.getDefault(), "%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return ip;
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();

        boolean isConnected = false;

        if (networkInfo != null && networkInfo.isConnected())
            isConnected = true;

        return isConnected;
    }
}
