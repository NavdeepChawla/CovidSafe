package com.example.android.covidhack.MainAppActivity.ContactActivity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import com.example.android.covidhack.MainAppActivity.ContactActivity.ContactActivity;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ActivityIntentService extends IntentService {
    protected static final String TAG = "Activity";
    //Call the super IntentService constructor with the name for the worker thread//

    //Temp variables
    private LocationManager lm;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    public ActivityIntentService() {
        super(TAG);
    }

    public static Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        gson=new Gson();
    }
//Define an onHandleIntent() method, which will be called whenever an activity detection update is available//

    @Override
    protected void onHandleIntent(Intent intent) {
        //Check whether the Intent contains activity recognition data//
        if (ActivityRecognitionResult.hasResult(intent)) {

            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            DetectedActivity mostProbableActivity = result.getMostProbableActivity();
            int activityType = mostProbableActivity.getType();
            String Activity=getActivityString(this,activityType);

            //Get an array of DetectedActivity objects//
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putString(ContactActivity.DETECTED_ACTIVITY,
                            detectedActivityToJson(Activity))
                    .apply();

        }
    }
//Convert the code for the detected activity type, into the corresponding string//

    static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.ON_BICYCLE:
                return  "BICYCLE";
            case DetectedActivity.ON_FOOT:
                return "FOOT";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.TILTING:
                return "TILTING";
            case DetectedActivity.WALKING:
                return "WALKING";
            case DetectedActivity.IN_VEHICLE:
                return "VEHICLE";
            default:
                return "UNKNOWN ACTIVITY";
        }
    }
    static final int[] POSSIBLE_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };

    static String detectedActivityToJson(String detectedActivity) {
        Type type = new TypeToken<String>() {}.getType();
        String json= gson.toJson(detectedActivity, type);
        return json;
    }
    static String detectedActivityFromJson(String jsonArray) {
        Type type = new TypeToken<String>() {}.getType();
        //String detectedActivity = gson.fromJson(jsonArray, type);
        String detectedActivity=jsonArray;
        if (detectedActivity == null) {
            detectedActivity = "";
        }
        return detectedActivity;
    }
}
