package com.manit.amit.bloodbank;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class AcceptFragment extends Fragment {

    private RecyclerView mUsersList;

    private DatabaseReference mDonorsDatabase;

    public FirebaseUser mCurrent_user;

    private LinearLayoutManager mLayoutManager;

    private TextView mWhenEmpty;

    private View mView;

    private DatabaseReference mTest;

    public AcceptFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mView = inflater.inflate(R.layout.fragment_accept, container, false);

        mWhenEmpty = mView.findViewById(R.id.whenempty);

        mCurrent_user= FirebaseAuth.getInstance().getCurrentUser();

        mDonorsDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user.getUid());

        mLayoutManager = new LinearLayoutManager(getActivity());

        mUsersList = (RecyclerView) mView.findViewById(R.id.users_list1);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(mLayoutManager);

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mTest = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user.getUid());

        mTest.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long i = dataSnapshot.getChildrenCount();
                String s = Long.toString(i);

                if(i == 0){

                    mWhenEmpty.setVisibility(View.VISIBLE);

                } else{

                    mWhenEmpty.setVisibility(View.INVISIBLE);
                }

                //Toast.makeText(getActivity(), s , Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerAdapter<Req_model, RequestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Req_model, RequestViewHolder>(

                Req_model.class,
                R.layout.request_single_layout,
                RequestViewHolder.class,
                mDonorsDatabase

        ) {

            @Override
            protected void populateViewHolder(RequestViewHolder viewHolder, Req_model model, int position) {

                viewHolder.setName(model.getFull_name());
                viewHolder.setNumber(model.getContact());
                viewHolder.setThumb_image(model.getThumb_image() , getActivity());

                final String user_id= getRef(position).getKey();

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(getActivity(), NewAcceptActivity.class);
                        profileIntent.putExtra("user_id1", user_id);
                        startActivity(profileIntent);
                    }
                });

            }
        };


        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public RequestViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.req_single_name);
            userNameView.setText(name);
        }

        public void setNumber(String contact) {
            TextView userContactView = (TextView) mView.findViewById(R.id.req_single_number);
            userContactView.setText(contact);
        }

        public void setThumb_image(String thumb_image, FragmentActivity activity) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.req_single_image);

            Picasso.with(activity).load(thumb_image).placeholder(R.drawable.developer).into(userImageView);
        }
    }


}
