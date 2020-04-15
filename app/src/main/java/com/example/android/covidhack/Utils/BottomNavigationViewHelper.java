package com.example.android.covidhack.Utils;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import com.example.android.covidhack.MainAppActivity.ContactActivity.ContactActivity;
import com.example.android.covidhack.MainAppActivity.MapActivity;
import com.example.android.covidhack.MainAppActivity.ProbabilityActivity;
import com.example.android.covidhack.MainAppActivity.ProfileActivity.ProfileViewActivity;
import com.example.android.covidhack.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BottomNavigationViewHelper {

    private static int count=0;
    private static final String TAG = "BottomNavigationViewHel";
    private static Intent intent1x;


    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.setTextVisibility(true);
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
    }

    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.bottom_nav_contact:
                        Intent intent1 = new Intent(context, ContactActivity.class);//ACTIVITY_NUM = 0
                        intent1.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        intent1.putExtra("Running",true);
                        context.startActivity(intent1);
                        break;

                    case R.id.bottom_nav_map:
                        Intent intent2  = new Intent(context, MapActivity.class);//ACTIVITY_NUM = 1
                        intent2.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent2);
                        break;

                    case R.id.bottom_nav_prob:
                        Intent intent3 = new Intent(context, ProbabilityActivity.class);//ACTIVITY_NUM = 2
                        intent3.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent3);
                        break;

                    case R.id.botton_nav_prof:
                        Intent intent4 = new Intent(context, ProfileViewActivity.class);//ACTIVITY_NUM = 3
                        intent4.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        context.startActivity(intent4);
                        break;

                }
                return false;
            }
        });
    }
}
