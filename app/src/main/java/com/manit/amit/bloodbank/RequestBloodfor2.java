package com.manit.amit.bloodbank;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class RequestBloodfor2 extends AppCompatActivity {

    public String s[] = new String[2];

    public String sfull_name;

    private static final String TAG = "abc";

    private Toolbar mToolbar;

    private RecyclerView mUsersList;

    public DatabaseReference mUserDatabase, mDonorsDatabase ;

    private FirebaseUser mCurrentUser;

    private LinearLayoutManager mLayoutManager;

    private TextView mCurText;

    private Switch mSwitch1, mSwitch2;

    public TextView mwhenempty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_bloodfor2);

        s = getIntent().getStringArrayExtra("data");

        Toast.makeText(RequestBloodfor2.this, s[0] + " " + s[1] , Toast.LENGTH_SHORT).show();

        mToolbar = (Toolbar) findViewById(R.id.requestappblood2);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Donors List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCurText = findViewById(R.id.CurText);

        mSwitch1 = findViewById(R.id.switch1);
        mSwitch2 = findViewById(R.id.switch2);

        mwhenempty = findViewById(R.id.whenempty);

        mCurText.setText("Displaying results for blood type " + s[1] + " at " + s[0]);
        mSwitch1.setText(s[1]);
        mSwitch2.setText("O-");


        mSwitch1.setChecked(true);
        mSwitch2.setChecked(false);
        mSwitch1.setEnabled(false);

        mDonorsDatabase = FirebaseDatabase.getInstance().getReference().child("Donors").child(s[0]).child(s[1]);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid().toString();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                sfull_name = dataSnapshot.child("full_name").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mLayoutManager = new LinearLayoutManager(this);

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

        calladapter();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mSwitch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){

                    mSwitch2.setEnabled(true);
                    mSwitch2.setChecked(false);
                    mSwitch1.setEnabled(false);

                    mCurText.setText("Displaying results for blood type " + s[1] + " at " + s[0]);

                    mDonorsDatabase = FirebaseDatabase.getInstance().getReference().child("Donors").child(s[0]).child(s[1]);
                    calladapter();

                }
            }
        });

        mSwitch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    mSwitch1.setEnabled(true);
                    mSwitch1.setChecked(false);
                    mSwitch2.setEnabled(false);

                    mCurText.setText("Displaying results for blood type O-" + " at " + s[0]);

                    mDonorsDatabase = FirebaseDatabase.getInstance().getReference().child("Donors").child(s[0]).child("B+");
                    calladapter();
                }

            }
        });

    }

    private void calladapter() {

        mDonorsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long i = dataSnapshot.getChildrenCount();
                String s = Long.toString(i);

                if(i == 0){

                    mwhenempty.setVisibility(View.VISIBLE);

                } else{

                    mwhenempty.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Donors_UsedForReqBlood, DonorsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Donors_UsedForReqBlood, DonorsViewHolder>(

                Donors_UsedForReqBlood.class,
                R.layout.donors_single_layout,
                DonorsViewHolder.class,
                mDonorsDatabase

        ) {
            @Override
            protected void populateViewHolder(DonorsViewHolder viewHolder, Donors_UsedForReqBlood model, int position) {

                if (model.getFull_name().equals(sfull_name)) {

                    viewHolder.Layout_hide();

                } else{

                    viewHolder.setName(model.getFull_name());
                    viewHolder.setNumber(model.getContact());
                    viewHolder.setUserImage(model.getThumb_image(), getApplicationContext());

                    final String user_id = getRef(position).getKey();


                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent profileIntent = new Intent(RequestBloodfor2.this, ProfileActivity.class);
                            profileIntent.putExtra("user_id", user_id);
                            startActivity(profileIntent);
                        }
                    });
                }

            }
        };

        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class DonorsViewHolder extends RecyclerView.ViewHolder{

        View mView;

        private final RelativeLayout layout;
        final RelativeLayout.LayoutParams params;

        public DonorsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            //to hide self row
            layout =(RelativeLayout) itemView.findViewById(R.id.rl1);
            params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.req_single_name);
            userNameView.setText(name);
        }

        public void setNumber(String contact) {

            TextView userContactView = (TextView) mView.findViewById(R.id.req_single_number);
            userContactView.setText(contact);
        }

        public void setUserImage(String thumb_image, Context applicationContext) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.req_single_image);

            Picasso.with(applicationContext).load(thumb_image).placeholder(R.drawable.developer).into(userImageView);

        }


        private void Layout_hide() {
            params.height = 0;
            layout.setLayoutParams(params);
        }
    }
}
