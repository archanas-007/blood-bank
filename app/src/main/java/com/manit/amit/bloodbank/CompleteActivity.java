package com.manit.amit.bloodbank;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompleteActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private Button mBtnComplete;

    public ProgressDialog mRegProgress;

    public DatabaseReference mFriendsDB , mFriendsDB1 , mFriendDB_del;

    public FirebaseUser mCurrent_user;

    public String friend_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        mRegProgress = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.complete_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Complete Donation Process");

        mBtnComplete = (Button) findViewById(R.id.Btn_complete);

        mRegProgress.setTitle("Loading Data");
        mRegProgress.setMessage("Please wait while we load data");
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.show();

        mBtnComplete.setEnabled(false);

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mFriendsDB = FirebaseDatabase.getInstance().getReference().child("Friends");

        mFriendsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mRegProgress.dismiss();

                if(dataSnapshot.hasChild(mCurrent_user.getUid().toString())){

                    mFriendsDB1 = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user.getUid().toString());

                    mFriendsDB1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(dataSnapshot.hasChild("friend")){

                                mBtnComplete.setEnabled(true);

                                friend_uid = dataSnapshot.child("friend").getValue().toString();

                                mBtnComplete.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        mFriendDB_del = FirebaseDatabase.getInstance().getReference("Friends").child(mCurrent_user.getUid().toString()).child("friend");
                                        mFriendDB_del.removeValue();

                                        mFriendDB_del = FirebaseDatabase.getInstance().getReference("Friends").child(friend_uid).child("friend");
                                        mFriendDB_del.removeValue();

                                        Toast.makeText(CompleteActivity.this, "Donation process has been successfully completed" , Toast.LENGTH_SHORT).show();
                                        mBtnComplete.setEnabled(false);

                                    }
                                });

                            } else{

                                Toast.makeText(CompleteActivity.this, "You have no one in your contact" , Toast.LENGTH_SHORT).show();

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else{
                    Toast.makeText(CompleteActivity.this, "You have no one in your contact" , Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
}
