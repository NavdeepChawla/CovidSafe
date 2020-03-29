package com.example.android.covidhack;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LanguageActivity extends AppCompatActivity {

    private Button proceed;
    private TextView english,marathi,hindi;
    private TextView seemore;
    private FrameLayout eng,hin,mar;
    int engstatus=0,hindstatus=0,marathistatus=0;
    int sum=0;

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
                if (sum==0){
                        engstatus++;
                        sum++;
                        eng.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==1) {
                    if (marathistatus == 1) {
                        mar.setBackgroundResource(R.color.white);
                        eng.setBackgroundResource(R.drawable.language_highlight);
                    } else if (hindstatus == 1) {
                        hin.setBackgroundResource(R.color.white);
                        eng.setBackgroundResource(R.drawable.language_highlight);
                    }else {
                        sum--;
                        eng.setBackgroundResource(R.color.white);
                    }
                }

            }
        });


        hindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sum==0){
                        hindstatus++;
                        sum++;
                        hin.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==1) {
                    if (marathistatus == 1) {
                        mar.setBackgroundResource(R.color.white);
                       hin.setBackgroundResource(R.drawable.language_highlight);
                    } else if (engstatus == 1) {
                        eng.setBackgroundResource(R.color.white);
                        hin.setBackgroundResource(R.drawable.language_highlight);
                    }else {
                        hin.setBackgroundResource(R.color.white);
                    }
                }
            }
        });

        marathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sum==0){
                        marathistatus++;
                        sum++;
                        mar.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==1) {
                    if (hindstatus == 1) {
                        hin.setBackgroundResource(R.color.white);
                       mar.setBackgroundResource(R.drawable.language_highlight);
                    } else if (engstatus == 1) {
                        eng.setBackgroundResource(R.color.white);
                       mar.setBackgroundResource(R.drawable.language_highlight);
                    }else{
                        mar.setBackgroundResource(R.color.white);
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
