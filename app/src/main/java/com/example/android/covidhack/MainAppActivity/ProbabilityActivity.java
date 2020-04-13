package com.example.android.covidhack;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ProbabilityActivity extends AppCompatActivity {

    private static final String TAG = "ProbabilityActivity";
    private Context mContext=ProbabilityActivity.this;
    private BottomNavigationViewEx bottomNavigationViewEx;
    private TextView topbartxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_probability);
        setupBottomNavigationView();
        topbartxt=(TextView)findViewById(R.id.topbartxt);
        topbartxt.setText("PEOPLE");
    }

    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
       bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);
    }
}
