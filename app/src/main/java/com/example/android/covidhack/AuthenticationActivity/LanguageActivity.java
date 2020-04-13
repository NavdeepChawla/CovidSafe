package com.example.android.covidhack;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LanguageActivity extends AppCompatActivity {

    private static final String TAG = "LanguageActivity";

    private Button proceed;
    private TextView english,marathi,hindi;
    private TextView seemore;
    private FrameLayout eng,hin,mar;
    boolean engstatus=false,hindstatus=false,marathistatus=false;
    boolean sum=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        proceed=(Button)findViewById(R.id.proceed);

        english=(TextView) findViewById(R.id.english);
        hindi=(TextView) findViewById(R.id.hindi);
        marathi=(TextView) findViewById(R.id.marathi);

        seemore=(TextView)findViewById(R.id.seemore);

        eng=(FrameLayout)findViewById(R.id.engbut);
        hin=(FrameLayout)findViewById(R.id.hinbut);
        mar=(FrameLayout)findViewById(R.id.marbut);

        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sum==false){
                        engstatus=true;
                        sum=true;
                        eng.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==true) {
                    if (marathistatus == true) {
                        engstatus=true;
                        marathistatus=false;
                        mar.setBackgroundResource(R.color.white);
                        eng.setBackgroundResource(R.drawable.language_highlight);
                    } else if (hindstatus == true) {
                        hindstatus=false;
                        engstatus=true;
                        hin.setBackgroundResource(R.color.white);
                        eng.setBackgroundResource(R.drawable.language_highlight);
                    }else if(engstatus ==true ){
                        sum=false;
                        engstatus=false;
                        eng.setBackgroundResource(R.color.white);
                    }
                }
            }
        });


        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sum==false){
                    hindstatus=true;
                    sum=true;
                    hin.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==true) {
                    if (marathistatus == true) {
                        hindstatus=true;
                        marathistatus=false;
                        mar.setBackgroundResource(R.color.white);
                        hin.setBackgroundResource(R.drawable.language_highlight);
                    } else if (hindstatus == true) {
                        hindstatus=false;
                        sum=false;
                        hin.setBackgroundResource(R.color.white);
                    }else if(engstatus ==true ){
                        hindstatus=true;
                        engstatus=false;
                        eng.setBackgroundResource(R.color.white);
                        hin.setBackgroundResource(R.drawable.language_highlight);
                    }
                }
            }
        });

        marathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sum==false){
                    marathistatus=true;
                    sum=true;
                    mar.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==true) {
                    if (marathistatus == true) {
                        sum=false;
                        marathistatus=false;
                        mar.setBackgroundResource(R.color.white);
                    } else if (hindstatus == true) {
                        hindstatus=false;
                        marathistatus=true;
                        hin.setBackgroundResource(R.color.white);
                        mar.setBackgroundResource(R.drawable.language_highlight);
                    }else if(engstatus ==true ){
                        marathistatus=true;
                        engstatus=false;
                        eng.setBackgroundResource(R.color.white);
                        mar.setBackgroundResource(R.drawable.language_highlight);
                    }
                }
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LanguageActivity.this,MobileActivity.class);
                startActivity(intent);
            }
        });
    }
}
