package com.manit.amit.bloodbank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    public String current_uid;

    private DatabaseReference mUserDatabase , mDonorDB;
    private FirebaseUser mCurrentUser;

    public String uid;

    public String email, password, phoneNumber;

    private Toolbar mToolbar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    private Button mLogin_btn;

    private ProgressDialog mLoginProgress, mOtpDialog;

    private FirebaseAuth mAuth;

    //declare otp
    private TextInputLayout mPhone, mOtp;
    private Button mVerify;
    private TextView mVerifyLabel;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    public int btnType=0;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        //for otp
        mPhone = (TextInputLayout) findViewById(R.id.phone_no);
        mOtp = (TextInputLayout) findViewById(R.id.otp);
        mVerify = (Button) findViewById(R.id.verify);
        mVerifyLabel = (TextView) findViewById(R.id.verify_label);

        mAuth = FirebaseAuth.getInstance();

        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);

        mLoginProgress = new ProgressDialog(this);
        mOtpDialog = new ProgressDialog(this);

        mLogin_btn = (Button) findViewById(R.id.login_btn);


        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = mLoginEmail.getEditText().getText().toString();
                password = mLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credentials.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email, password);

                }

            }
        });

    }

    private void LoginAgain(String email, final String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    //Do here
                    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

                    current_uid = mCurrentUser.getUid().toString();

                    mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

                    mOtpDialog.dismiss();

                    mUserDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String sfname = dataSnapshot.child("fname").getValue().toString();
                            String slname = dataSnapshot.child("lname").getValue().toString();
                            String sgender = dataSnapshot.child("gender").getValue().toString();
                            String sage = dataSnapshot.child("age").getValue().toString();
                            String sweight = dataSnapshot.child("weight").getValue().toString();
                            String sblood = dataSnapshot.child("blood").getValue().toString();
                            String scontact = dataSnapshot.child("contact").getValue().toString();
                            String saddress = dataSnapshot.child("address").getValue().toString();
                            String scity = dataSnapshot.child("city").getValue().toString();
                            String sstate = dataSnapshot.child("state").getValue().toString();
                            String simage = dataSnapshot.child("image").getValue().toString();

                            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child("contact");

                            mDonorDB = FirebaseDatabase.getInstance().getReference().child("Donors").child(scity).child(sblood).child(current_uid).child("contact");
                            mDonorDB.setValue(phoneNumber);
                            mUserDatabase.setValue(phoneNumber);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else{
                    mOtpDialog.hide();
                    Toast.makeText(LoginActivity.this, "Cannot Sign In. Please check the Details and Try Again.",Toast.LENGTH_LONG ).show();

                }

            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = task.getResult().getUser();
                            mAuth.signOut();

                            LoginAgain(email, password);

                        } else {
                            // Sign in failed, display a message and update the UI

                            //mErrorText.setVisibility(View.VISIBLE);
                            Toast.makeText(LoginActivity.this, "There was some error in verification!!!", Toast.LENGTH_LONG).show();

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();

                    //email sign out
                    FirebaseAuth.getInstance().signOut();

                    //visibity
                    mLogin_btn.setEnabled(false);
                    mPhone.setVisibility(View.VISIBLE);
                    mOtp.setVisibility(View.VISIBLE);
                    mVerify.setVisibility(View.VISIBLE);
                    mVerifyLabel.setVisibility(View.VISIBLE);

                    //for otp
                    mVerify.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if(btnType == 0) {
                                mVerify.setEnabled(false);

                                phoneNumber = mPhone.getEditText().getText().toString();

                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        phoneNumber,
                                        60,
                                        TimeUnit.SECONDS,
                                        LoginActivity.this,
                                        mCallbacks
                                );
                            }
                            else{

                                mVerify.setEnabled(true);
                                mOtpDialog.setTitle("Logging In");
                                mOtpDialog.setMessage("Please wait while we check your credentials.");
                                mOtpDialog.setCanceledOnTouchOutside(false);
                                mOtpDialog.show();

                                String verificationCode = mOtp.getEditText().getText().toString();

                                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                                signInWithPhoneAuthCredential(credential);

                            }

                        }
                    });

                    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        @Override
                        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                            signInWithPhoneAuthCredential(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(FirebaseException e) {

                            Toast.makeText(LoginActivity.this, "There was some error in verification!!!", Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onCodeSent(String verificationId,
                                               PhoneAuthProvider.ForceResendingToken token) {


                            // Save verification ID and resending token so we can use them later

                            mVerificationId = verificationId;
                            mResendToken = token;
                            btnType=1;

                            mVerify.setText("Verify Code");
                            mVerify.setEnabled(true);
                        }

                    };


                } else{
                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, "Cannot Sign In. Please check the Details and Try Again.",Toast.LENGTH_LONG ).show();

                }

            }
        });
    }
}
