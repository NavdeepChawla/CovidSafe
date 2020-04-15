package com.example.android.covidhack.MainAppActivity.ProfileActivity;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class ProfileViewActivity extends AppCompatActivity {

    private static final String TAG = "ProfileViewActivity";
    private Context mContext=ProfileViewActivity.this;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private TextView name,place,dob,email,toptxt;
    private FirebaseFirestore db;
    private ImageView barcode;

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
        String number=user.getPhoneNumber();

        DocumentReference docRef = db.collection("Profile").document(number);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        name.setText(""+document.get("Name"));
                        dob.setText(""+document.get("DateOfBirth"));
                        email.setText(""+document.get("Email"));
                        place.setText(""+document.get("Home"));
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
