package com.example.sreeshav.prog02;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

/**
 * Created by sreeshav on 2/29/16.
 */

public class WatchListenerService extends WearableListenerService {
    private static final String Clicked = "/Clicked";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equalsIgnoreCase("/RAINING")) {
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Intent intent = new Intent(this, MainView.class);
            intent.putExtra("VAL", value);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //you need to add this flag since you're starting a new activity from a service
            startActivity(intent);
        }
    }
}
