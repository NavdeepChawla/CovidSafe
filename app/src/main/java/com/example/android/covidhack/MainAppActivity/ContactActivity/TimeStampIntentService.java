package com.example.android.covidhack.MainAppActivity.ContactActivity;

import android.app.IntentService;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;

public class TimeStampIntentService extends IntentService {

    private static final String TAG = "TimeStampIntentService";

    public TimeStampIntentService() {
        super(TAG);
    }

    public static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        gson=new Gson();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Date currentTimeobj = Calendar.getInstance().getTime();
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentTimeobj);
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentTimeobj);
        String currentDateandTime = currentDate+" at "+currentTime;

        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(ContactActivity.DETECTED_TIME,
                        detectedTimeToJson(currentDateandTime))
                .apply();
    }

    static String detectedTimeToJson(String currentDateandTime) {
        Type type = new TypeToken<String>() {}.getType();
        String json= gson.toJson(currentDateandTime, type);
        return json;
    }
    static String detectedTimeFromJson(String jsonArray) {
        Type type = new TypeToken<String>() {}.getType();
        //String detectedActivity = gson.fromJson(jsonArray, type);
        String detectedDateandTime=jsonArray;
        if (detectedDateandTime == null) {
            detectedDateandTime = "";
        }
        return detectedDateandTime;
    }
}
