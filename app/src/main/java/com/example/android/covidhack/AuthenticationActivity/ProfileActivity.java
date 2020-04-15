package com.example.android.covidhack.AuthenticationActivity;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.android.covidhack.MainAppActivity.ContactActivity.ContactActivity;
import com.example.android.covidhack.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private Button saveproc;
    private EditText name,email,dob,home;
    private FirebaseFirestore database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name=(EditText)findViewById(R.id.nameinput);
        email=(EditText)findViewById(R.id.emailinput);
        dob=(EditText)findViewById(R.id.dateofbirth);
        home=(EditText)findViewById(R.id.hometown);

        database=FirebaseFirestore.getInstance();

        Intent intent=getIntent();
        final String number=intent.getStringExtra("mobile");

        saveproc=(Button)findViewById(R.id.saveandproceed);
        saveproc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nm=name.getText().toString();
                String em=email.getText().toString();
                String db=dob.getText().toString();
                String hm=home.getText().toString();
                String uuid="Covid"+UUID.randomUUID().toString()+"Safe";

                Map<String,Object> users=new  HashMap<>();
                users.put("Name",nm);
                users.put("Email",em);
                users.put("DateOfBirth",db);
                users.put("Mobile",number);
                users.put("Home",hm);
                users.put("Probability",0);
                users.put("ID",uuid);

                SharedPreferences.Editor editor = getSharedPreferences(getPackageName(), MODE_PRIVATE).edit();

                //Encryption Keys
                KeyPair kp = getKeyPair();
                //Public Key
                PublicKey publicKey = kp.getPublic();
                byte[] publicKeyBytes = publicKey.getEncoded();
                String publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));
                editor.putString("PublicKey",publicKeyBytesBase64);

                //PrivateKey
                PrivateKey privateKey = kp.getPrivate();
                byte[] privateKeyBytes = privateKey.getEncoded();
                String privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));
                editor.putString("PrivateKey",privateKeyBytesBase64);

                //bluetoothName
                editor.putString("UUID",uuid);
                editor.apply();

                database.collection("Profile").document(number).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
                database.collection("Identify").document(uuid).set(number);

                Intent intent=new Intent(ProfileActivity.this, ContactActivity.class);
                intent.putExtra("phnumber",number);
                startActivity(intent);
            }
        });
    }

    public static KeyPair getKeyPair() {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            kp = kpg.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kp;
    }
}
