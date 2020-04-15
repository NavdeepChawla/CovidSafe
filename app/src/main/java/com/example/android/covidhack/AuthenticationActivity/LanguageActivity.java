package com.example.android.covidhack.AuthenticationActivity;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.covidhack.R;

public class LanguageActivity extends AppCompatActivity {

    private static final String TAG = "LanguageActivity";

    private Button proceed;
    private TextView english,dutch,german,french,spanish;
    private FrameLayout eng,dut,ger,fre,spa;
    boolean engstatus=false;
    boolean gerstatus=false,dutstatus=false,frestatus=false,spastatus=false;
    boolean sum=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        proceed=(Button)findViewById(R.id.proceed);

        english=(TextView) findViewById(R.id.english);
        dutch=(TextView)findViewById(R.id.dutch);
        german=(TextView)findViewById(R.id.german);
        french=(TextView)findViewById(R.id.french);
        spanish=(TextView)findViewById(R.id.spanish);


        eng=(FrameLayout)findViewById(R.id.engbut);
        dut=(FrameLayout)findViewById(R.id.dutbut);
        ger=(FrameLayout)findViewById(R.id.gerbut);
        fre=(FrameLayout)findViewById(R.id.frebut);
        spa=(FrameLayout)findViewById(R.id.spabut);

        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sum){
                        engstatus=true;
                        sum=true;
                        eng.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==true) {
                    if (dutstatus == true) {
                        engstatus=true;
                        dutstatus=false;
                        dut.setBackgroundResource(R.color.white);
                        eng.setBackgroundResource(R.drawable.language_highlight);
                    } else if (gerstatus == true) {
                        gerstatus=false;
                        engstatus=true;
                        ger.setBackgroundResource(R.color.white);
                        eng.setBackgroundResource(R.drawable.language_highlight);
                    }else if(frestatus== true) {
                        frestatus=false;
                        engstatus=true;
                        fre.setBackgroundResource(R.color.white);
                        eng.setBackgroundResource(R.drawable.language_highlight);
                    }else if(spastatus== true) {
                        spastatus=false;
                        engstatus=true;
                        spa.setBackgroundResource(R.color.white);
                        eng.setBackgroundResource(R.drawable.language_highlight);
                    }
                    else if(engstatus ==true ){
                        sum=false;
                        engstatus=false;
                        eng.setBackgroundResource(R.color.white);
                    }
                }
            }
        });


        dut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sum==false){
                    dutstatus=true;
                    sum=true;
                    dut.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==true) {
                    if (engstatus == true) {
                        engstatus=false;
                        dutstatus=true;
                        eng.setBackgroundResource(R.color.white);
                        dut.setBackgroundResource(R.drawable.language_highlight);
                    } else if (gerstatus == true) {
                        gerstatus=false;
                        dutstatus=true;
                        ger.setBackgroundResource(R.color.white);
                        dut.setBackgroundResource(R.drawable.language_highlight);
                    }else if(frestatus== true) {
                        frestatus=false;
                        dutstatus=true;
                        fre.setBackgroundResource(R.color.white);
                        dut.setBackgroundResource(R.drawable.language_highlight);
                    }else if(spastatus== true) {
                        spastatus=false;
                        dutstatus=true;
                        spa.setBackgroundResource(R.color.white);
                        dut.setBackgroundResource(R.drawable.language_highlight);
                    }
                    else if(dutstatus ==true ){
                        sum=false;
                        dutstatus=false;
                        eng.setBackgroundResource(R.color.white);
                    }
                }
            }
        });

       ger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sum==false){
                    gerstatus=true;
                    sum=true;
                    ger.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==true) {
                    if (engstatus == true) {
                        engstatus=false;
                        gerstatus=true;
                        eng.setBackgroundResource(R.color.white);
                        ger.setBackgroundResource(R.drawable.language_highlight);
                    } else if (dutstatus == true) {
                        gerstatus=true;
                        dutstatus=false;
                        dut.setBackgroundResource(R.color.white);
                        ger.setBackgroundResource(R.drawable.language_highlight);
                    }else if(frestatus== true) {
                        frestatus=false;
                        gerstatus=true;
                        fre.setBackgroundResource(R.color.white);
                        ger.setBackgroundResource(R.drawable.language_highlight);
                    }else if(spastatus== true) {
                        spastatus=false;
                        gerstatus=true;
                        spa.setBackgroundResource(R.color.white);
                        ger.setBackgroundResource(R.drawable.language_highlight);
                    }
                    else if(gerstatus ==true ){
                        sum=false;
                        gerstatus=false;
                        ger.setBackgroundResource(R.color.white);
                    }
                }
            }
        });

        fre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sum==false){
                    frestatus=true;
                    sum=true;
                    fre.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==true) {
                    if (engstatus == true) {
                        engstatus=false;
                        frestatus=true;
                        eng.setBackgroundResource(R.color.white);
                        fre.setBackgroundResource(R.drawable.language_highlight);
                    } else if (dutstatus == true) {
                        frestatus=true;
                        dutstatus=false;
                        dut.setBackgroundResource(R.color.white);
                        fre.setBackgroundResource(R.drawable.language_highlight);
                    }else if(gerstatus== true) {
                        frestatus=true;
                        gerstatus=false;
                        ger.setBackgroundResource(R.color.white);
                        fre.setBackgroundResource(R.drawable.language_highlight);
                    }else if(spastatus== true) {
                        spastatus=false;
                        frestatus=true;
                        spa.setBackgroundResource(R.color.white);
                        fre.setBackgroundResource(R.drawable.language_highlight);
                    }
                    else if(frestatus ==true ){
                        sum=false;
                        frestatus=false;
                        fre.setBackgroundResource(R.color.white);
                    }
                }
            }
        });

        spa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sum==false){
                    spastatus=true;
                    sum=true;
                    spa.setBackgroundResource(R.drawable.language_highlight);
                }else if(sum==true) {
                    if (engstatus == true) {
                        engstatus=false;
                        spastatus=true;
                        eng.setBackgroundResource(R.color.white);
                        spa.setBackgroundResource(R.drawable.language_highlight);
                    } else if (dutstatus == true) {
                        spastatus=true;
                        dutstatus=false;
                        dut.setBackgroundResource(R.color.white);
                        spa.setBackgroundResource(R.drawable.language_highlight);
                    }else if(gerstatus== true) {
                        spastatus=true;
                        gerstatus=false;
                        ger.setBackgroundResource(R.color.white);
                        spa.setBackgroundResource(R.drawable.language_highlight);
                    }else if(frestatus== true) {
                        spastatus=true;
                        frestatus=false;
                        fre.setBackgroundResource(R.color.white);
                        spa.setBackgroundResource(R.drawable.language_highlight);
                    }
                    else if(spastatus ==true ){
                        sum=false;
                        spastatus=false;
                        spa.setBackgroundResource(R.color.white);
                    }
                }
            }
        });



        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sum){
                    Toast toast=Toast.makeText(getApplicationContext(),R.string.lang_error,Toast.LENGTH_SHORT);
                    toast.show();
                }else if(sum) {
                    Intent intent = new Intent(LanguageActivity.this, MobileActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}
