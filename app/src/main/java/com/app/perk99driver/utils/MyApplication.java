package com.app.perk99driver.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.gson.JsonObject;

import java.util.ArrayList;


public class MyApplication extends MultiDexApplication {


    private static MyApplication instance;




    private static Activity mActivity;

    public static MyApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;

        try {
            FirebaseApp.initializeApp(this);
        }
        catch (Exception e) {
        }




    }

    public static void setActivity(Activity act){
        mActivity=act;
    }

    public static Activity getActivity(){
        return mActivity;
    }




    public static boolean hasNetwork() {
        return instance.checkIfHasNetwork();
    }

    public boolean checkIfHasNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
