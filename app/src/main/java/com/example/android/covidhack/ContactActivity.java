package com.example.android.covidhack;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Calendar;

public class ContactActivity extends AppCompatActivity {

    private static final String TAG = "ContactActivity";

    private static final int PERMISSIONS_REQUEST = 1;
    private LocationManager lm;
    private Context mcontext=ContactActivity.this;

    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public ArrayList<location> gpstrack = new ArrayList<>();
    BluetoothAdapter mBluetoothAdapter;

    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseFirestore db;
    private Map<String,Object> recurdata;

    private TextView blue;
    private String emp="";
    private String number;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    //When discoverability is turned on/off
    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    //Device is in Discoverable Mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Enabled.");
                        break;
                    //Device not in discoverable mode
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Able to receive connections.");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "mBroadcastReceiver2: Discoverability Disabled. Not able to receive connections.");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "mBroadcastReceiver2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "mBroadcastReceiver2: Connected.");
                        break;
                }

            }
        }
    };

    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //TODO pass the list of discovered devices to firebase
                mBTDevices.add(device);
                Map<String,Object> bluetooth=new HashMap<>();
                bluetooth.put("name",device.getName());
                bluetooth.put("address"," "+device.getAddress());
                emp+=" "+device.getAddress();
                List<String> arrblue=new ArrayList<>();
                arrblue.add(""+device.getAddress());
                recurdata.put("Bluetooth",arrblue);
                /**db.collection("contact").add(bluetooth)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });**/
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        recurdata=new HashMap<>();

        if(user==null) {
            Intent intent = getIntent();
            String phnumber = intent.getStringExtra("phnumber");
            number=phnumber;
            recurdata.put("number",number);
        }

        db = FirebaseFirestore.getInstance();


        //blue.setText(emp);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        final Handler handler = new Handler();

        /**
         // Check GPS is enabled
         lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
         Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
         finish();
         }

         // Check location permission is granted - if it is, start
         // the service, otherwise request the permission
         int permission = ContextCompat.checkSelfPermission(this,
         Manifest.permission.ACCESS_FINE_LOCATION);

         if (permission == PackageManager.PERMISSION_GRANTED) {
         testtext.setText("Bye");
         lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ContactActivity.this);
         /**LocationService lc=new LocationService(this);
         Location location=lc.getLocation();
         testtext.setText(" "+location.getLatitude()+location.getLongitude());
         } else {
         ActivityCompat.requestPermissions(this,
         new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
         PERMISSIONS_REQUEST);
         }
         **/

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                Log.d("Handlers", "Called on main thread");
                // Repeat this the same runnable code block again another 2 minute
                // 'this' is referencing the Runnable object
                btnDiscover();
                if (ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mcontext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(ContactActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    location lm=new location(location.getLatitude(),location.getLongitude(),location.getAccuracy());
                                    gpstrack.add(lm);
                                    Map<String,Object> locobject=new HashMap<>();
                                    locobject.put("Lattitude",location.getLatitude());
                                    locobject.put("Longitude",location.getLongitude());
                                    locobject.put("Accuracy",location.getAccuracy());
                                    emp+=" "+location.getLatitude()+" "+location.getLongitude();
                                    List<Double> arr=new ArrayList<>();
                                    arr.add(location.getLatitude());
                                    arr.add(location.getLongitude());
                                    recurdata.put("Location",arr);
                                    Date Time = Calendar.getInstance().getTime();
                                    String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Time);
                                    String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Time);
                                    String currentDateandTime = currentDate+" at "+currentTime;
                                    emp+=" "+currentDateandTime+" ";
                                    recurdata.put("TimeStamps",currentDateandTime);
                                    db.collection("Devices").add(recurdata)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });
                                 }
                            }
                        });
                handler.postDelayed(this, 1000*60);
            }
        };

        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);

    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver3);
        mBluetoothAdapter.cancelDiscovery();
    }


    private void startTrackerService() {
        startService(new Intent(this, MyService.class));
        finish();
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

}
