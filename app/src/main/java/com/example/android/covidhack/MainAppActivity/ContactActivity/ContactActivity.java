package com.example.android.covidhack.MainAppActivity.ContactActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.os.Handler;

import com.example.android.covidhack.AuthenticationActivity.MainActivity;
import com.example.android.covidhack.R;
import com.example.android.covidhack.Utils.BottomNavigationViewHelper;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ContactActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener{


    private static final String TAG = "ContactActivity";


    private LocationManager lm;
    private Context mcontext=ContactActivity.this;


    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    BluetoothAdapter mBluetoothAdapter;

    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseFirestore db;
    private ActivityRecognitionClient activityRecognitionClient;

    private Map<String,Object> recurdata;
    private Map<String,Object> firebaseresult;

    private BottomNavigationViewEx bottomNavigationViewEx;

    //private FirebaseIntentReciever firebaseIntentReciever;

    private TextView prob;
    private TextView blue;
    private String emp="";
    private String number;

    public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";
    public static final String DETECTED_TIME = ".DETECTED_TIME";

    private TextView txtlat,txtlong,txtact,txtppl;

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!mBTDevices.contains(device)) {
                    mBTDevices.add(device);
                }
                for(BluetoothDevice bd:mBTDevices){
                    //txtlistblue=" "+bd.getAddress()+"\n";
                }
                txtppl.setText("People around you: "+mBTDevices.size());
                List<String> arrblue = new ArrayList<>();
                arrblue.add(""+device.getAddress());
                recurdata.put("Bluetooth",arrblue);
                //txtblue.setText(txtlistblue);
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        txtlat=(TextView)findViewById(R.id.txt_lattitude);
        txtlong=(TextView)findViewById(R.id.txt_longitude);
        txtact=(TextView)findViewById(R.id.txt_activity);
        txtppl=(TextView)findViewById(R.id.txt_ppl_around);
        prob=(TextView)findViewById(R.id.prob);


        requestLocationPermission();
        // Start the initial runnable task by posting through the handler

        Intent intent=new Intent(this,FirebaseIntentService.class);
        startForegroundService(intent);

        Intent myintent=getIntent();
        final boolean Running = myintent.getBooleanExtra("Running",true);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recurdata=new HashMap<>();
        number =user.getPhoneNumber();

        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Profile").document(number);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        int probx= Integer.parseInt(document.get("Probability").toString());
                        prob.setText(""+probx+"%");
                        if(probx>75)
                            prob.setTextColor(getResources().getColor(R.color.prob75));
                        else if(probx>40)
                            prob.setTextColor(getResources().getColor(R.color.prob40));
                        else if(probx>15)
                            prob.setTextColor(getResources().getColor(R.color.prob15));
                        else if(probx>5)
                            prob.setTextColor(getResources().getColor(R.color.prob5));
                        else
                            prob.setTextColor(getResources().getColor(R.color.prob0));

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });



        //blue.setText(emp);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        activityRecognitionClient=new ActivityRecognitionClient(this);




        activityRecognitionClient.requestActivityUpdates(
                1000*40 ,
                getActivityDetectionPendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateDetectedActivitiesList();
            }
        });


        //IntentFilter filter = new IntentFilter();
        //filter.addCategory(Intent.CATEGORY_DEFAULT);
        //firebaseIntentReciever= new FirebaseIntentReciever();
        //registerReceiver(firebaseIntentReciever, filter);

        //Intent fireintent=new Intent(ContactActivity.this,FirebaseIntentService.class);
        //startService(fireintent);


        final Handler timecheck=new Handler();
        /**
        Runnable timecheckRunnable=new Runnable() {
            @Override
            public void run() {
                timecheck.postDelayed(this, 1000);
                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("ss");
                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
                int sec=Integer.parseInt(dateFormatGmt.format(new Date())+"");

                SimpleDateFormat minute = new SimpleDateFormat("mm");
                minute.setTimeZone(TimeZone.getTimeZone("GMT"));
                int min=Integer.parseInt(minute.format(new Date())+"");

                if (min%2!=0) {
                    if (sec==30) {
                        btnDiscover();

                        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
                        String deviceName = myDevice.getName();

                        recurdata.put("Uniqueid",deviceName);

                        if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        fusedLocationClient.getLastLocation()
                                .addOnSuccessListener(ContactActivity.this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        // Got last known location. In some rare situations this can be null.
                                        if (location != null) {
                                            // Logic to handle location object
                                            List<Double> arr = new ArrayList<>();
                                            double lat_d, long_d;
                                            lat_d = location.getLatitude();
                                            long_d = location.getLongitude();
                                            arr.add(lat_d);
                                            arr.add(long_d);
                                            recurdata.put("Location", arr);
                                            txtlat.setText("Lat : " + lat_d);
                                            txtlong.setText("Long : " + long_d);
                                            Date currentTimeobj = Calendar.getInstance().getTime();
                                            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentTimeobj);
                                            String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentTimeobj);
                                            String currentDateandTime = currentDate + " at " + currentTime;
                                            recurdata.put("TimeStamps", currentDateandTime);
                                            long dateInsecs = (currentTimeobj.getTime()) / 1000;
                                            db.collection("Profile").document(number).collection("TimeStamps").document("" + dateInsecs).set(recurdata)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            //blue = (TextView) findViewById(R.id.ded);
                                                            //blue.setText("" + recurdata.get("Activity"));
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: ");
                                                }
                                            });
                                        }
                                    }
                                });

                    }
                }
            }
        };

        timecheck.post(timecheckRunnable);
        **/

        String detectedActivity= ActivityIntentService.detectedActivityFromJson(
                PreferenceManager.getDefaultSharedPreferences(this)
                .getString(DETECTED_ACTIVITY,""));
        detectedActivity=detectedActivity.replace("\"","");
        detectedActivity=detectedActivity.replace("\"","");
        recurdata.put("Activity",detectedActivity);
        txtact.setText(detectedActivity);


        setupBottomNavigationView();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        //unregisterReceiver(firebaseIntentReciever);
        //unregisterReceiver(mBroadcastReceiver3);
        //mBluetoothAdapter.cancelDiscovery();
    }



    public void btnDiscover() {
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if(!mBluetoothAdapter.isDiscovering()){

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     *
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationViewEx=(BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mcontext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        updateDetectedActivitiesList();
    }
    @Override
    protected void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    //Get a PendingIntent//
    private PendingIntent getActivityDetectionPendingIntent() {
        //Send the activity data to our DetectedActivitiesIntentService class//
        Intent intent = new Intent(this, ActivityIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    //Process the list of activities//
    protected void updateDetectedActivitiesList() {
        String detectedActivity=ActivityIntentService.detectedActivityFromJson(
                PreferenceManager.getDefaultSharedPreferences(mcontext)
                        .getString(DETECTED_ACTIVITY,"")
        );

        //mAdapter.updateActivities(detectedActivity);
        detectedActivity=detectedActivity.replace("\"","");
        detectedActivity=detectedActivity.replace("\"","");
        recurdata.put("Activity",detectedActivity);
        txtact.setText(detectedActivity);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(DETECTED_ACTIVITY)) {
            updateDetectedActivitiesList();
        }
    }

    /**
    public class FirebaseIntentReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            firebaseresult=(HashMap<String, Object>)intent.getSerializableExtra("Fire");
            mBTDevices=intent.getParcelableArrayListExtra("blue");
            List<Double> arr=new ArrayList<>();
            arr= (ArrayList<Double>)firebaseresult.get("Location");
            txtlat.setText("Lat : "+ arr.get(0));
            txtlong.setText("Long : "+arr.get(1));
            txtppl.setText("People around you: "+mBTDevices.size());
        }
    }
     **/



    private void requestLocationPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Location Permission")
                        .setMessage("Please kindly select the permission for always")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActivityCompat.requestPermissions(ContactActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
                            }
                        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        }
        else
        {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(ContactActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001);
            }
        }
    }

    private void requestSMSPermission()
    {
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ContactActivity.this,new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 1002);
        }
    }

    private void requestStoragePermission()
    {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(ContactActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1003);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case 1001:if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                requestLocationPermission();
            }
            else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                requestSMSPermission();
            }
                break;
            case 1002:if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                requestSMSPermission();
            }
            else if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                requestStoragePermission();
            }
                break;
            case 1003:if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED))
            {
                requestStoragePermission();
            }
                break;
        }
    }

}
