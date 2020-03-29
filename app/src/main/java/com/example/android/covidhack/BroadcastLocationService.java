package com.example.android.covidhack;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.content.Context;
import android.location.Location;

public class BroadcastLocationService extends IntentService {
    public BroadcastLocationService() {
        super("BroadcastLocationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendMessage(getApplicationContext(),
                new LocationService(getApplicationContext()).getLocation());
    }

    private void sendMessage(Context mContext, Location location) {

    }
}
