package com.manit.amit.bloodbank;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    public String sfullname, scontact, sthumb_image;

    private TextView mProfileName, mProfileConatct, mProfileAddress, mProfileBlood;

    private ImageView mProfileImage;

    private DatabaseReference mFirend_req;
    private DatabaseReference mUserDatabase,mCurr_database;
    private DatabaseReference mFirend_req_received;
    private DatabaseReference mNotficationDatabase;

    private Button mReqBtn;
    private String mcurr_state;
    private FirebaseUser mCurrent_user;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mProfileName = (TextView) findViewById(R.id.profile_name);
        mProfileConatct = (TextView) findViewById(R.id.profile_contact);
        mProfileAddress = (TextView) findViewById(R.id.profile_address);
        mReqBtn = (Button) findViewById(R.id.profile_request);
        mProfileImage = (ImageView) findViewById(R.id.profile_image);
        mProfileBlood = findViewById(R.id.profile_blood);

        mCurrent_user= FirebaseAuth.getInstance().getCurrentUser();

        mcurr_state = "not_friends";
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mCurr_database = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_user.getUid());

        mFirend_req=FirebaseDatabase.getInstance().getReference().child("Blood_req");
        mFirend_req_received = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        //mFirend_req_received = FirebaseDatabase.getInstance().getReference().child("Friend_req");

        mNotficationDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");

        mProgressDialog = new ProgressDialog(ProfileActivity.this);
        mProgressDialog.setTitle("Loading Donor Data");
        mProgressDialog.setMessage("Please wait while we load the donor profile");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String sfname = dataSnapshot.child("fname").getValue().toString();
                String slname = dataSnapshot.child("lname").getValue().toString();
                String scontact = dataSnapshot.child("contact").getValue().toString();
                String saddress = dataSnapshot.child("address").getValue().toString();
                String scity = dataSnapshot.child("city").getValue().toString();
                String sstate = dataSnapshot.child("state").getValue().toString();
                String sblood = dataSnapshot.child("blood").getValue().toString();
                String simage = dataSnapshot.child("image").getValue().toString();
                mProfileName.setText(sfname + " " + slname);
                mProfileConatct.setText("Phone : " + scontact);
                mProfileAddress.setText(saddress + " ," + scity + " ," + sstate);
                mProfileBlood.setText("Blood Type : " + sblood);

                Picasso.with(ProfileActivity.this).load(simage).placeholder(R.drawable.default_pic).into(mProfileImage);

                mProgressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //mProfileID = (TextView) findViewById(R.id.DisplayText);
        //mProfileID.setText(user_id);
        mReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mReqBtn.setEnabled(false);

                if(mcurr_state.equals("not_friends"))
                {
                    mFirend_req.child(mCurrent_user.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                mCurr_database.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {

                                        HashMap<String, String> notificationData = new HashMap<>();
                                        notificationData.put("from", mCurrent_user.getUid());
                                        notificationData.put("type" , "request");

                                        mNotficationDatabase.child(user_id).setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                sfullname = dataSnapshot.child("full_name").getValue().toString();
                                                scontact = dataSnapshot.child("contact").getValue().toString();
                                                sthumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                                                mFirend_req_received.child(user_id).child(mCurrent_user.getUid()).child("full_name").setValue(sfullname);
                                                mFirend_req_received.child(user_id).child(mCurrent_user.getUid()).child("contact").setValue(scontact);
                                                mFirend_req_received.child(user_id).child(mCurrent_user.getUid()).child("thumb_image").setValue(sthumb_image);

                                                Toast.makeText(ProfileActivity.this, "Request sent successfully" , Toast.LENGTH_SHORT).show();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            }
                            else{
                                Toast.makeText(ProfileActivity.this,"Sending Blood Request Failed",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

    }
}
