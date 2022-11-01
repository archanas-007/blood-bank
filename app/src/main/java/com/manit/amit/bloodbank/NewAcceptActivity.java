package com.manit.amit.bloodbank;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewAcceptActivity extends AppCompatActivity {

    private TextView mname, mcontact, maddress;
    private Button mbtnaccept, mbtndecline;
    private ImageView mProfile;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mBloodreqDel;
    private DatabaseReference mFriendreqDel;
    private DatabaseReference mFriendsAdd1;
    private DatabaseReference mFriendsAdd2;
    private DatabaseReference mNotification;

    public FirebaseUser mCurrent_user;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_accept);

        final String user_id = getIntent().getStringExtra("user_id1");

        mname = findViewById(R.id.new_name1);
        mcontact = findViewById(R.id.new_contact);
        maddress = findViewById(R.id.new_address);
        mbtnaccept = findViewById(R.id.btn_accept);
        mbtndecline = findViewById(R.id.btn_decline);
        mProfile = findViewById(R.id.new_image);

        mCurrent_user= FirebaseAuth.getInstance().getCurrentUser();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        mProgressDialog = new ProgressDialog(NewAcceptActivity.this);
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
                String simage = dataSnapshot.child("image").getValue().toString();

                mname.setText(sfname + " " + slname);
                mcontact.setText(scontact);
                maddress.setText(saddress + "\n" + scity + "\n" + sstate);

                Picasso.with(NewAcceptActivity.this).load(simage).placeholder(R.drawable.default_pic).into(mProfile);

                mProgressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    mbtnaccept.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Toast.makeText(NewAcceptActivity.this, "You have accepted the request" , Toast.LENGTH_LONG).show();

            String D_curr_str;

            SimpleDateFormat DateFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");

            Date date_curr = new Date();

            D_curr_str = DateFormat.format(date_curr);

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrent_user.getUid());
            mUserDatabase.child("date").setValue(D_curr_str);


            mbtnaccept.setEnabled(false);
            mbtndecline.setEnabled(false);


            mBloodreqDel = FirebaseDatabase.getInstance().getReference().child("Blood_req").child(user_id);
            mFriendreqDel = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user.getUid());
            mFriendsAdd1 = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id);
            mFriendsAdd2 = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user.getUid());
            mFriendsAdd2 = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user.getUid());
            mNotification = FirebaseDatabase.getInstance().getReference().child("Notifications").child(mCurrent_user.getUid());


            mBloodreqDel.removeValue();
            mFriendreqDel.removeValue();
            mNotification.removeValue();

            mFriendsAdd1.child("friend").setValue(mCurrent_user.getUid());
            //mFriendsAdd1.child("state").setValue("friends");
            //mFriendsAdd2.child("state").setValue("friends");
            mFriendsAdd2.child("friend").setValue(user_id);

        }
    });

    mbtndecline.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {


            Toast.makeText(NewAcceptActivity.this, "You have declined the request" , Toast.LENGTH_LONG).show();

            mbtndecline.setEnabled(false);
            mbtnaccept.setEnabled(false);

            mFriendreqDel = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user.getUid()).child(user_id);
            mBloodreqDel = FirebaseDatabase.getInstance().getReference().child("Blood_req").child(user_id).child(mCurrent_user.getUid());
            mNotification = FirebaseDatabase.getInstance().getReference().child("Notifications").child(mCurrent_user.getUid());


            mBloodreqDel.removeValue();
            mFriendreqDel.removeValue();
            mNotification.removeValue();

        }
    });

    }
}
