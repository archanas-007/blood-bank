package com.manit.amit.bloodbank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChangedetailsActivity extends AppCompatActivity {

    public String current_uid;

    private Toolbar mToolbar;
    private ProgressDialog mRegProgress;

    public String simage , sthumb;

    private DatabaseReference mUserDatabase , mDonorDB, mDBdel;
    private FirebaseUser mCurrentUser;

    //buttons
    private TextInputLayout mfname,mlname,mgender,mage,mweight,mblood,mcontact,maddress,mcity,mstate;
    private Button msend;

    private Spinner mBloodSpinner;
    private Spinner mGenderSpinner;
    private Spinner mCitySpinner;

    public String sBlood, sGender, sCity, sdate, sisdonor, sblood, scity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changedetails);

        mRegProgress = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.changedetails_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Details");

        //mRegProgress.setTitle("Loading Details");
        //mRegProgress.setMessage("Please wait while we are loading your Details");
        //mRegProgress.setCanceledOnTouchOutside(false);
        //mRegProgress.show();

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

        mBloodSpinner = findViewById(R.id.spinnerblood);
        mGenderSpinner = findViewById(R.id.spinnerGender);
        mCitySpinner = findViewById(R.id.spinnerCity);

        msend = (Button) findViewById(R.id.sendbtn);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        current_uid = mCurrentUser.getUid().toString();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ChangedetailsActivity.this, R.array.blood_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBloodSpinner.setAdapter(adapter);

        final ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(ChangedetailsActivity.this, R.array.genderlist, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mGenderSpinner.setAdapter(adapter1);

        final ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(ChangedetailsActivity.this, R.array.cities, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCitySpinner.setAdapter(adapter2);

        mBloodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                sBlood = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sGender = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mCitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sCity = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String sfname = dataSnapshot.child("fname").getValue().toString();
                String slname = dataSnapshot.child("lname").getValue().toString();
                String sgender = dataSnapshot.child("gender").getValue().toString();
                String sage = dataSnapshot.child("age").getValue().toString();
                String sweight = dataSnapshot.child("weight").getValue().toString();
                sblood = dataSnapshot.child("blood").getValue().toString();
                String scontact = dataSnapshot.child("contact").getValue().toString();
                String saddress = dataSnapshot.child("address").getValue().toString();
                scity = dataSnapshot.child("city").getValue().toString();
                String sstate = dataSnapshot.child("state").getValue().toString();
                sdate = dataSnapshot.child("date").getValue().toString();
                sisdonor = dataSnapshot.child("isdonor").getValue().toString();
                simage = dataSnapshot.child("image").getValue().toString();
                sthumb = dataSnapshot.child("thumb_image").getValue().toString();

                sBlood = sblood;
                sGender = sgender;
                sCity = scity;

                String fullname = sfname + " " + slname;
                String fulladdress = saddress + " , " + scity + " , " + sstate;
                String sfullblood = "Blood Type : " + sblood;

                mfname.getEditText().setText(sfname);
                mlname.getEditText().setText(slname);
                //mgender.getEditText().setText(sgender);
                mage.getEditText().setText(sage);
                mweight.getEditText().setText(sweight);
                //mblood.getEditText().setText(sblood);
                mcontact.getEditText().setText(scontact);
                maddress.getEditText().setText(saddress);
                //mcity.getEditText().setText(scity);
                mstate.getEditText().setText(sstate);

                if (sBlood != null) {
                    int spinnerPosition = adapter.getPosition(sBlood);
                    mBloodSpinner.setSelection(spinnerPosition);
                }

                if (sGender != null) {
                    int spinnerPosition = adapter1.getPosition(sGender);
                    mGenderSpinner.setSelection(spinnerPosition);
                }

                if (sCity != null) {
                    int spinnerPosition = adapter2.getPosition(sCity);
                    mCitySpinner.setSelection(spinnerPosition);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sfname = mfname.getEditText().getText().toString();
                String slname = mlname.getEditText().getText().toString();
                //String sgender = mgender.getEditText().getText().toString();
                String sage = mage.getEditText().getText().toString();
                String sweight = mweight.getEditText().getText().toString();
                //String sblood = mblood.getEditText().getText().toString();
                String scontact = mcontact.getEditText().getText().toString();
                String saddress = maddress.getEditText().getText().toString();
                //String scity = mcity.getEditText().getText().toString();
                String sstate = mstate.getEditText().getText().toString();

                mRegProgress.setTitle("Submitting Details");
                mRegProgress.setMessage("Please wait while we are processing");
                mRegProgress.setCanceledOnTouchOutside(false);
                mRegProgress.show();

                mDBdel = FirebaseDatabase.getInstance().getReference("Donors").child(scity).child(sblood).child(current_uid);
                mDBdel.removeValue();


                HashMap<String, String > userMap = new HashMap<>();
                userMap.put("fname", sfname);
                userMap.put("lname", slname);
                userMap.put("gender", sGender);
                userMap.put("age", sage);
                userMap.put("weight", sweight);
                userMap.put("blood", sBlood);
                userMap.put("contact", scontact);
                userMap.put("address", saddress);
                userMap.put("city", sCity);
                userMap.put("state", sstate);
                userMap.put("image", simage);
                userMap.put("thumb_image", sthumb);
                userMap.put("full_name", sfname + " " + slname);

                if(sisdonor.equals("1")){
                    mDonorDB = FirebaseDatabase.getInstance().getReference().child("Donors").child(sCity).child(sBlood).child(current_uid);
                    mDonorDB.setValue(userMap);
                }

                userMap.put("isdonor", sisdonor);
                userMap.put("date", sdate);

                mUserDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task .isSuccessful()){
                            mRegProgress.dismiss();

                            Intent mainIntent = new Intent(ChangedetailsActivity.this, SettingActivity.class);
                            //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();

                        }
                    }
                });



            }
        });

    }

}
