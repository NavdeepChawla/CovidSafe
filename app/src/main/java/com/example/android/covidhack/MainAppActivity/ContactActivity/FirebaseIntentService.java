package com.example.android.covidhack.MainAppActivity.ContactActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.covidhack.R;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Cipher;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class FirebaseIntentService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "";
    private ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    private Map<String,Object> Data=new HashMap<>();

    private ActivityRecognitionClient activityRecognitionClient;
    public static final String DETECTED_ACTIVITY = ".DETECTED_ACTIVITY";

    //Broadcast Receiver
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");
            assert action != null;
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!mBTDevices.contains(device)) {
                    mBTDevices.add(device);
                }
            }
        }
    };



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //For Notification of Service.
        String channelID= UUID.randomUUID().toString();
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(channelID, "ServiceTest", NotificationManager.IMPORTANCE_NONE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
        Notification notification =new NotificationCompat.Builder(getApplicationContext(),channelID).setContentTitle("Test").build();
        //To keep the service alive
        startForeground(startId,notification);


        activityRecognitionClient=new ActivityRecognitionClient(this);

        activityRecognitionClient.requestActivityUpdates(
                1000*40 ,
                getActivityDetectionPendingIntent()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateDetectedActivitiesList();
            }
        });
        //Bluetooth Adapter
        BluetoothAdapter mBluetoothAdapter;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String bluetoothName=prefs.getString("UUID","");
        mBluetoothAdapter.setName(bluetoothName);
        if(!mBluetoothAdapter.isEnabled())
        {
            mBluetoothAdapter.enable();
        }
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }

        //For Location
        final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;

        final CountDownTimer checktime=new CountDownTimer(120000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                SimpleDateFormat dateFormatGmt = new SimpleDateFormat("ss");
                dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
                int sec=Integer.parseInt(dateFormatGmt.format(new Date())+"");

                SimpleDateFormat minute = new SimpleDateFormat("mm");
                minute.setTimeZone(TimeZone.getTimeZone("GMT"));
                int min=Integer.parseInt(minute.format(new Date())+"");

                if (min%2!=0) {
                    if (sec==30) {
                        //Countdown Timer
                        final CountDownTimer countDownTimer = new CountDownTimer(1000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }
                            @Override
                            public void onFinish() {
                                //Firebase Auth
                                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                assert user != null;
                                final String number =user.getPhoneNumber();
                                //final String number ="+919911008666";


                                //Database Reference
                                FirebaseFirestore firestore=FirebaseFirestore.getInstance();

                                //TimeStamp
                                Date currentTimeobj = Calendar.getInstance().getTime();
                                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(currentTimeobj);
                                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(currentTimeobj);
                                String currentDateandTime = currentDate+" at "+currentTime;
                                long dateInsecs = (currentTimeobj.getTime())/1000;
                                Data.put("TimeStamps",currentDateandTime);

                                //Location
                                List<Double> exactLocation=new ArrayList<>();
                                double latitude=0.0;
                                double longitude=0.0;
                                try {
                                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        //TODO
                                    }
                                    Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                                    if(location!=null)
                                    {
                                        latitude=(double)Math.round(location.getLatitude() * 1000d) / 1000d;
                                        longitude=(double)Math.round(location.getLongitude()*1000d)/1000d;
                                    }
                                    else{
                                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                        if(location!=null)
                                        {
                                            latitude=(double)Math.round(location.getLatitude() * 1000d) / 1000d;
                                            longitude=(double)Math.round(location.getLongitude()*1000d)/1000d;
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                exactLocation.add(latitude);
                                exactLocation.add(longitude);
                                try
                                {
                                    String tempLat=Double.toString(latitude);
                                    ContactActivity.txtlat.setText(tempLat);
                                    String tempLong=Double.toString(longitude);
                                    ContactActivity.txtlong.setText(tempLong);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                String loc=""+latitude+","+longitude;
                                Data.put("Location",TestEncryptData(loc));


                                //Disconnecting Bluetooth Adapter
                                try{
                                    unregisterReceiver(mBroadcastReceiver3);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                //Pushing Data on Firebase
                                try{
                                    if(mBTDevices.size()!=0)
                                    {
                                        List<String> macAddress=new ArrayList<>();
                                        for(int i=0;i<mBTDevices.size();i++)
                                        {
                                            String tempMacAddress=mBTDevices.get(i).getName();
                                            macAddress.add(tempMacAddress);
                                        }
                                        Data.put("BluetoothName",macAddress);
                                    }
                                    firestore.collection("Profile").document(number).collection("TimeStamps").document("" + dateInsecs).set(Data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                                //Restarting the service
                                Intent restartService = new Intent(getApplicationContext(),FirebaseIntentService.class);
                                startForegroundService(restartService);
                            }
                        };
                        countDownTimer.start();
                    }
                }

            }

            @Override
            public void onFinish() {

            }
        };

        checktime.start();

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDestroy() {
        try
        {
            unregisterReceiver(mBroadcastReceiver3);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        Intent restartService = new Intent(getApplicationContext(),FirebaseIntentService.class);
        startForegroundService(restartService);
        super.onDestroy();
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
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString(DETECTED_ACTIVITY,"")
        );

        //mAdapter.updateActivities(detectedActivity);
        detectedActivity=detectedActivity.replace("\"","");
        detectedActivity=detectedActivity.replace("\"","");
        Data.put("Activity",detectedActivity);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(DETECTED_ACTIVITY)) {
            updateDetectedActivitiesList();
        }
    }

    public String TestEncryptData(String dataToEncrypt) {

        //Retrieve Public Key
        SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String publicKey=prefs.getString("PublicKey","");

        //Encryption
        return encryptRSAToString(dataToEncrypt, publicKey);
    }

    public static String encryptRSAToString(String clearText, String publicKey) {
        /*
        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePublic(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedBase64.replaceAll("(\\r|\\n)", "");
         */
        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            PublicKey key = keyFac.generatePublic(keySpec);

            // get an RSA cipher object and print the provider
            //final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");

            final Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA-512AndMGF1Padding");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedBase64;//.replaceAll("(\\r|\\n)", "");
    }

    /**
    public static String decryptRSAToString(String encryptedBase64, String privateKey) {

        String decryptedString = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedString;
    }
     **/

}
