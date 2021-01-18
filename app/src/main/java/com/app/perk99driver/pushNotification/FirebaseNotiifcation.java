package com.app.perk99driver.pushNotification;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseNotiifcation extends FirebaseMessagingService
{

/*    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);
      //  Log.e("RemoteMessage ",remoteMessage+"");
//        Log.e("RemoteMessageData ",remoteMessage.getData()+"");

     *//*   if (remoteMessage.getData().size() != 0)
        {
            Log.e("inside ", String.valueOf(remoteMessage.getData()));
            handleNotification(remoteMessage.getData().get("body"), remoteMessage.getData());
        }*//*
    }*/


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
//        Log.e("RemoteMessage ",remoteMessage.getData().toString());
    }
}
