package com.example.android.covidhack;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

public class VerificationActivity extends AppCompatActivity implements TextWatcher {

    private Button btn;
    private EditText et1, et2, et3, et4,et5,et6;

    private FirebaseAuth mAuth;
    private String mobile;
    private String mVerificationId;

    private Context mcontext=VerificationActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        mAuth = FirebaseAuth.getInstance();
        Intent intent=getIntent();
        mobile=intent.getStringExtra("number");

        sendVerificationCode(mobile);


        btn = (Button) findViewById(R.id.submitotp);
        et1 = (EditText) findViewById(R.id.otp1);
        et2 = (EditText) findViewById(R.id.otp2);
        et3 = (EditText) findViewById(R.id.otp3);
        et4 = (EditText) findViewById(R.id.otp4);
        et5=(EditText) findViewById(R.id.otp5);
        et6=(EditText) findViewById(R.id.otp6);

        et1.addTextChangedListener(this);
        et2.addTextChangedListener(this);
        et3.addTextChangedListener(this);
        et4.addTextChangedListener(this);
        et5.addTextChangedListener(this);
        et6.addTextChangedListener(this);

        final String otp=""+et1.getText()+et2.getText()+et3.getText()+et4.getText()+et5.getText()+et6.getText();

        /**
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VerificationActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
         **/

        //if the automatic sms detection did not work, user can also enter the code manually
        //so adding a click listener to the button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = otp;
                //verifying the code entered manually
                verifyVerificationCode(code);
            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (editable.length() == 1) {
            if (et1.length() == 1) {
                et2.requestFocus();
            }

            if (et2.length() == 1) {
                et3.requestFocus();
            }
            if (et3.length() == 1) {
                et4.requestFocus();
            }
            if(et4.length()==1){
                et5.requestFocus();
            }
            if(et5.length()==1){
               et6.requestFocus();
            }
        } else if (editable.length() == 0) {
            if(et6.length() == 0){
                et5.requestFocus();
            }
            if(et5.length() == 0){
                et4.requestFocus();
            }
            if (et4.length() == 0) {
                et3.requestFocus();
            }
            if (et3.length() == 0) {
                et2.requestFocus();
            }
            if (et2.length() == 0) {
                et1.requestFocus();
            }
        }

    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mobile,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }

    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
               et1.setText(""+code.charAt(0));
               et2.setText(""+code.charAt(1));
               et3.setText(""+code.charAt(2));
               et4.setText(""+code.charAt(3));
               et5.setText(""+code.charAt(4));
               et6.setText(""+code.charAt(5));
                //verifying the code
                verifyVerificationCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(mcontext, e.getMessage()+mobile, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
        }
    };


    private void verifyVerificationCode(String code) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(mcontext, ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("mobile",mobile);
                            startActivity(intent);

                        } else {
                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }
}
