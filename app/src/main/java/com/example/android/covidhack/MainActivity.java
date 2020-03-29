package com.example.android.covidhack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_started);
        //button=(Button) findViewById(R.id.getstartedbtn);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            setContentView(R.layout.activity_contact);
        } else {
            // No user is signed in
            setContentView(R.layout.activity_started);
            button=(Button)findViewById(R.id.getstartedbtn);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(MainActivity.this,LanguageActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
