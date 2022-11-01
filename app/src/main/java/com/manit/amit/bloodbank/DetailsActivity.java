package com.manit.amit.bloodbank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    public String sgender, sblood, scity;

    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;

    private DatabaseReference mDatabase , mDonorDB;

    private CheckBox mcheckbox;

    public String uid;

    //buttons
    private TextInputLayout mfname,mlname,mgender,mage,mweight,mblood,mcontact,maddress,mcity,mstate;
    private Button msend;

    private Spinner mBloodSpinner;
    private Spinner mGenderSpinner;
    private Spinner mCitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        mRegProgress = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.details_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Enter your Details");


        mfname = (TextInputLayout) findViewById(R.id.fname);
        mlname = (TextInputLayout) findViewById(R.id.lname);
        //mgender = (TextInputLayout) findViewById(R.id.gender);
        mage = (TextInputLayout) findViewById(R.id.age);
        mweight = (TextInputLayout) findViewById(R.id.weight);
        //mblood = (TextInputLayout) findViewById(R.id.blood);
        mcontact = (TextInputLayout) findViewById(R.id.contact);
        maddress = (TextInputLayout) findViewById(R.id.address);
        //mcity = (TextInputLayout) findViewById(R.id.city);
        mstate = (TextInputLayout) findViewById(R.id.state);

        mcheckbox = (CheckBox) findViewById(R.id.checkDonor);

        msend = (Button) findViewById(R.id.detailsBtn);

        mBloodSpinner = (Spinner) findViewById(R.id.Blood_type_spinner);
        mGenderSpinner = findViewById(R.id.Gender_spinner);
        mCitySpinner = findViewById(R.id.city_type_spinner);

        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        uid = current_user.getUid();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(DetailsActivity.this, R.array.blood_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBloodSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(DetailsActivity.this, R.array.genderlist, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(DetailsActivity.this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCitySpinner.setAdapter(adapter2);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        mBloodSpinner.setOnItemSelectedListener(DetailsActivity.this);
        mGenderSpinner.setOnItemSelectedListener(DetailsActivity.this);
        mCitySpinner.setOnItemSelectedListener(DetailsActivity.this);

        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sfname = mfname.getEditText().getText().toString();
                String slname = mlname.getEditText().getText().toString();
                String sage = mage.getEditText().getText().toString();
                String sweight = mweight.getEditText().getText().toString();
                String scontact = mcontact.getEditText().getText().toString();
                String saddress = maddress.getEditText().getText().toString();
                //String scity = mcity.getEditText().getText().toString();
                String sstate = mstate.getEditText().getText().toString();
                String simage = "default_pic";
                String sthumb_image = "default_thumb";

                mDonorDB = FirebaseDatabase.getInstance().getReference().child("Donors").child(scity).child(sblood).child(uid);

                mRegProgress.setTitle("Submitting Details");
                mRegProgress.setMessage("Please wait while we are processing");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();


                HashMap<String, String > userMap = new HashMap<>();
                userMap.put("fname", sfname);
                userMap.put("lname", slname);
                userMap.put("gender", sgender);
                userMap.put("age", sage);
                userMap.put("weight", sweight);
                userMap.put("blood", sblood);
                userMap.put("contact", scontact);
                userMap.put("address", saddress);
                userMap.put("city", scity);
                userMap.put("state", sstate);
                userMap.put("image", simage);
                userMap.put("thumb_image", sthumb_image);
                userMap.put("full_name", sfname + " " + slname);

                if(mcheckbox.isChecked()){

                    mDonorDB.setValue(userMap);
                    userMap.put("isdonor", "1");

                } else{

                    userMap.put("isdonor", "0");
                }


                userMap.put("date", "10/10/2013 11:30:10");

                mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mRegProgress.dismiss();

                            FirebaseAuth.getInstance().signOut();

                            Intent mainIntent = new Intent(DetailsActivity.this, LoginActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }
                    }
                });

            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Spinner spin = (Spinner)adapterView;
        Spinner spin1 = (Spinner)adapterView;
        Spinner spin2 = (Spinner)adapterView;

        if(spin.getId() == R.id.Gender_spinner)
        {
            String sSelected = adapterView.getItemAtPosition(i).toString();
            sgender=sSelected;
            //Toast.makeText(this, "Your choose :" + sSelected,Toast.LENGTH_SHORT).show();
        }

        if(spin1.getId() == R.id.Blood_type_spinner)
        {
            String sSelected = adapterView.getItemAtPosition(i).toString();
            sblood=sSelected;
            //Toast.makeText(this, "Your choose :" + sSelected,Toast.LENGTH_SHORT).show();
        }

        if(spin2.getId() == R.id.city_type_spinner)
        {
            String sSelected = adapterView.getItemAtPosition(i).toString();
            scity=sSelected;
            //Toast.makeText(this, "Your choose :" + sSelected,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
