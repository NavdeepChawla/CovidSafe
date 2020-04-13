package com.example.android.covidhack;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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

                Map<String,Object> users=new  HashMap<>();
                users.put("Name",nm);
                users.put("Email",em);
                users.put("DateOfBirth",db);
                users.put("Mobile",number);
                users.put("Home",hm);
                users.put("Probability",0);

                database.collection("Profile").document(number).set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


                Intent intent=new Intent(ProfileActivity.this, ContactActivity.class);
                intent.putExtra("phnumber",number);
                startActivity(intent);
            }
        });
    }
}
