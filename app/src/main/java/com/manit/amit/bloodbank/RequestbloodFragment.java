package com.manit.amit.bloodbank;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
    public class RequestbloodFragment extends Fragment implements View.OnClickListener{

    public static final String TAG="abc";

    public int curr_selected;

    public String lastSelected1="A+",lastSelected2="Agartala";

    private RadioGroup mRadioGrp;
    private RadioButton mRadioBtn;

    private TextView mInputBlood;
    private TextView mInputCity;

    public String stringlist[] = new String[2];

    private ProgressDialog mProgress;

    public int ctr;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private Button mrequestbtn;

    private View mView;

    private Spinner mBloodSpinner;

    //For Update Date

    public FirebaseUser mCurrentUser1;
    public DatabaseReference mUserDatabase1, mDonorDB1, mDonorDBdel1;
    public String scity, sblood;

    private Spinner mCitySpinnerF;

    public RequestbloodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ctr=0;

        mView =  inflater.inflate(R.layout.fragment_requestblood, container, false);

        // Inflate the layout for this fragment

        mInputBlood = (TextView) mView.findViewById(R.id.InputBlood);
        mInputCity = (TextView) mView.findViewById(R.id.TextViewCity);

        mrequestbtn = (Button) mView.findViewById(R.id.request_btn);

        mBloodSpinner = (Spinner) mView.findViewById(R.id.Blood_spinner);
        mCitySpinnerF = (Spinner) mView.findViewById(R.id.City_spinner);

        mRadioGrp = (RadioGroup) mView.findViewById(R.id.Radio_choice);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid().toString();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        UpdateDate();

        mInputBlood.setEnabled(false);
        mInputCity.setEnabled(false);
        mBloodSpinner.setEnabled(false);
        mCitySpinnerF.setEnabled(false);

        //to show dialog box for a certain duration
        /*if(ctr==0) {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setTitle("Loading Data");
            mProgress.setMessage("Please wait while we load your Account Details");
            mProgress.setCanceledOnTouchOutside(false);
            mProgress.show();

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    mProgress.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 3000);
            ctr++;
        }*/

        int selected_item = mRadioGrp.getCheckedRadioButtonId();
        mRadioBtn = (RadioButton) mView.findViewById(selected_item);

        mRadioGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if(i == R.id.selfReceive){

                    mProgress = new ProgressDialog(getActivity());
                    mProgress.setTitle("Loading Data");
                    mProgress.setMessage("Please wait while we load your Account Details");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();

                    curr_selected = i;

                    mInputBlood.setEnabled(false);
                    mInputCity.setEnabled(false);
                    mBloodSpinner.setEnabled(false);
                    mCitySpinnerF.setEnabled(false);

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

                            //Toast.makeText(getActivity(), sblood, Toast.LENGTH_LONG).show();

                            stringlist[0]=scity;
                            stringlist[1]=sblood;

                            mProgress.cancel();

                            Toast.makeText(getActivity(), stringlist[0] + " " + stringlist[1] , Toast.LENGTH_SHORT).show();


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else{

                    curr_selected = i;

                    mInputBlood.setEnabled(true);
                    mInputCity.setEnabled(true);
                    mBloodSpinner.setEnabled(true);
                    mCitySpinnerF.setEnabled(true);

                    stringlist[1]=lastSelected1;
                    stringlist[0]=lastSelected2;

                    Toast.makeText(getActivity(), stringlist[0] + " " + stringlist[1] , Toast.LENGTH_SHORT).show();

                    mBloodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String sSelected = adapterView.getItemAtPosition(i).toString();

                            stringlist[1] = lastSelected1 = sSelected;

                            //Toast.makeText(getActivity(), stringlist[0] + " " + stringlist[1] , Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    mCitySpinnerF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String sSelected = adapterView.getItemAtPosition(i).toString();

                            stringlist[0] = lastSelected2 = sSelected;

                            Toast.makeText(getActivity(), stringlist[0] + " " + stringlist[1] , Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }

            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.blood_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBloodSpinner.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.cities, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCitySpinnerF.setAdapter(adapter1);

        mrequestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(curr_selected == R.id.othersReceive){

                    if(stringlist[1].equals("O+") || stringlist[1].equals("A-") || stringlist[1].equals("B-")){

                        Intent askSelfBlood = new Intent(getActivity(), RequestBloodfor2.class);
                        askSelfBlood.putExtra("data", stringlist);
                        getActivity().startActivity(askSelfBlood);

                    } else if(stringlist[1].equals("A+") || stringlist[1].equals("B+") || stringlist[1].equals("AB-")){

                        Intent askSelfBlood = new Intent(getActivity(), RequestBloodfor4.class);
                        askSelfBlood.putExtra("data", stringlist);
                        getActivity().startActivity(askSelfBlood);

                    } else if(stringlist[1].equals("O-")){

                        Intent askSelfBlood = new Intent(getActivity(), RequestBloodfor1.class);
                        askSelfBlood.putExtra("data", stringlist);
                        getActivity().startActivity(askSelfBlood);

                    } else{

                        Intent askSelfBlood = new Intent(getActivity(), RequestBloodfor6.class);
                        askSelfBlood.putExtra("data", stringlist);
                        getActivity().startActivity(askSelfBlood);

                    }

                } else if(curr_selected == R.id.selfReceive){

                    if(stringlist[1].equals("O+") || stringlist[1].equals("A-") || stringlist[1].equals("B-")){

                        Intent askSelfBlood = new Intent(getActivity(), RequestBloodfor2.class);
                        askSelfBlood.putExtra("data", stringlist);
                        getActivity().startActivity(askSelfBlood);

                    } else if(stringlist[1].equals("A+") || stringlist[1].equals("B+") || stringlist[1].equals("AB-")){

                        Intent askSelfBlood = new Intent(getActivity(), RequestBloodfor4.class);
                        askSelfBlood.putExtra("data", stringlist);
                        getActivity().startActivity(askSelfBlood);

                    } else if(stringlist[1].equals("O-")){

                        Intent askSelfBlood = new Intent(getActivity(), RequestBloodfor1.class);
                        askSelfBlood.putExtra("data", stringlist);
                        getActivity().startActivity(askSelfBlood);

                    } else{

                        Intent askSelfBlood = new Intent(getActivity(), RequestBloodfor6.class);
                        askSelfBlood.putExtra("data", stringlist);
                        getActivity().startActivity(askSelfBlood);

                    }

                } else{

                    Toast.makeText(getActivity(), "Select an option first !!!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        return mView;
    }

    private void UpdateDate() {

        {

            mCurrentUser1 = FirebaseAuth.getInstance().getCurrentUser();

            mUserDatabase1 = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser1.getUid());

            mUserDatabase1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Date D = null, D_cur = null;
                    String D_curr_str;

                    String date = dataSnapshot.child("date").getValue().toString();
                    String isdonor = dataSnapshot.child("isdonor").getValue().toString();
                    scity = dataSnapshot.child("city").getValue().toString();
                    sblood = dataSnapshot.child("blood").getValue().toString();

                    int int_isdonor = Integer.parseInt(isdonor);

                    SimpleDateFormat DateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

                    try {
                        D = DateFormat.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Date date_curr = new Date();
                    D_curr_str = DateFormat.format(date_curr);
                    try {
                        D_cur = DateFormat.parse(D_curr_str);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long elapsedDays = Diff(D, D_cur);

                    if(elapsedDays > 90 && int_isdonor == 1){

                        String sfname = dataSnapshot.child("fname").getValue().toString();
                        String slname = dataSnapshot.child("lname").getValue().toString();
                        String sgender = dataSnapshot.child("gender").getValue().toString();
                        String sage = dataSnapshot.child("age").getValue().toString();
                        String sweight = dataSnapshot.child("weight").getValue().toString();
                        String sblood1 = dataSnapshot.child("blood").getValue().toString();
                        String scontact = dataSnapshot.child("contact").getValue().toString();
                        String saddress = dataSnapshot.child("address").getValue().toString();
                        String scity1 = dataSnapshot.child("city").getValue().toString();
                        String sstate = dataSnapshot.child("state").getValue().toString();
                        String simage = dataSnapshot.child("image").getValue().toString();
                        String sthumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                        HashMap<String, String > userMap1 = new HashMap<>();
                        userMap1.put("fname", sfname);
                        userMap1.put("lname", slname);
                        userMap1.put("gender", sgender);
                        userMap1.put("age", sage);
                        userMap1.put("weight", sweight);
                        userMap1.put("blood", sblood1);
                        userMap1.put("contact", scontact);
                        userMap1.put("address", saddress);
                        userMap1.put("city", scity1);
                        userMap1.put("state", sstate);
                        userMap1.put("image", simage);
                        userMap1.put("thumb_image", sthumb_image);
                        userMap1.put("full_name", sfname + " " + slname);

                        mDonorDB1 = FirebaseDatabase.getInstance().getReference().child("Donors").child(scity).child(sblood).child(mCurrentUser1.getUid());
                        mDonorDB1.setValue(userMap1);

                        Log.i(TAG, Long.toString(elapsedDays));

                    } else if(elapsedDays <= 90 && int_isdonor == 1){

                        Log.i(TAG, "elsepart");


                        mDonorDB1 = FirebaseDatabase.getInstance().getReference().child("Donors").child(scity).child(sblood);
                        mDonorDB1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(mCurrentUser1.getUid().toString())){

                                    mDonorDBdel1 = FirebaseDatabase.getInstance().getReference().child("Donors").child(scity).child(sblood).child(mCurrentUser1.getUid());
                                    mDonorDBdel1.removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private long Diff(Date startDate, Date endDate) {

        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;

        return elapsedDays;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.request_btn :
                Intent askSelfBlood = new Intent(getActivity(), RequestBloodfor2.class);
                getActivity().startActivity(askSelfBlood);
                break;

        }

    }
}
