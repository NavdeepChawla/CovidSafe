package com.example.android.covidhack.MainAppActivity.ProfileActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.covidhack.Utils.BottomNavigationViewHelper;
import com.example.android.covidhack.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ProfileViewActivity extends AppCompatActivity {

    private static final String TAG = "ProfileViewActivity";
    private Context mContext=ProfileViewActivity.this;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private TextView name,place,dob,email,toptxt;
    private FirebaseFirestore db;
    private ImageView barcode;
    private DocumentSnapshot document;
    private Map<String,Object> users=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);

        name=(TextView)findViewById(R.id.profilename);
        place=(TextView)findViewById(R.id.profileplace);
        dob=(TextView)findViewById(R.id.profiledob);
        email=(TextView)findViewById(R.id.profileemail);
        toptxt=(TextView)findViewById(R.id.topbartxt);
        toptxt.setText("PROFILE");
        barcode=(ImageView)findViewById(R.id.barcodeimage);

        setupBottomNavigationView();

        db=FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        final String number=user.getPhoneNumber();

        DocumentReference docRef = db.collection("Profile").document(number);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name.setText(""+document.get("Name"));
                        dob.setText(""+document.get("DateOfBirth"));
                        email.setText(""+document.get("Email"));
                        place.setText(""+document.get("Home"));
                        users.put("Name",document.get("Name"));
                        users.put("Email",document.get("Email"));
                        users.put("DateOfBirth",document.get("DateOfBirth"));
                        users.put("Mobile",document.get("Mobile"));
                        users.put("Home",document.get("Home"));
                        users.put("Probability",document.get("Probability"));
                        users.put("ID",document.get("ID"));
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        String serializeString = new Gson().toJson(number);
        Bitmap bitmap = QRCodeHelper.newInstance(this).setContent(serializeString)
                        .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
                        .setMargin(2).getQRCOde();
        barcode.setImageBitmap(bitmap);

        Button PrivateKey=findViewById(R.id.privateKey);
        PrivateKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(getPackageName(), MODE_PRIVATE);
                String privateKey=prefs.getString("PrivateKey","");
                users.put("PrivateKey",privateKey);
                db.collection("Profile").document(number).set(users);
            }
        });

        }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);
    }
}
