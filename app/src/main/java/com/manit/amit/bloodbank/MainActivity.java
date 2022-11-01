package com.manit.amit.bloodbank;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
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

public class MainActivity extends AppCompatActivity {
    public int flag=0;

    public static final String TAG = "abc";

    public String sfrom , sfull_name;

    private FirebaseAuth mAuth;
    private Toolbar mToolbar;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private TabLayout mTabLayout;

    NotificationCompat.Builder notification;
    private static final int uniqueID = 456123;

    private DatabaseReference mNotifDatabase, mNotifDB, mUserDatabse;
    private FirebaseUser mCurrentUser;

    private FloatingActionMenu floatingActionMenu;
    private com.github.clans.fab.FloatingActionButton maccountSettings, mcontactDonor, mcompleteDonation, mdirectionsBB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Blood Bank");

        //tabs
        mViewPager = (ViewPager) findViewById(R.id.tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);

        NotificationFn();

        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.floatingActionMenu);
        maccountSettings = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.profileAction);
        mcontactDonor = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.contactDonor);
        mcompleteDonation = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.complete);
        mdirectionsBB = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.directionBB);

        maccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingintent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(settingintent);
            }
        });

        mcontactDonor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent share = new Intent(MainActivity.this, ShareLocationActivity.class);
                startActivity(share);

            }
        });

        mcompleteDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent complete = new Intent(MainActivity.this, CompleteActivity.class);
                startActivity(complete);

            }
        });

        mdirectionsBB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent directions = new Intent(MainActivity.this, ContactUsActivity.class);
                startActivity(directions);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        Context context = getBaseContext();

        PackageManager pm = context.getPackageManager();
        int writeperm = pm.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName());
        int readperm = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, context.getPackageName());
        int callperm = pm.checkPermission(Manifest.permission.CALL_PHONE, context.getPackageName());
        int locperm1 = pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName());
        int locperm2 = pm.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context.getPackageName());

        if (    writeperm != PackageManager.PERMISSION_GRANTED ||
                readperm != PackageManager.PERMISSION_GRANTED ||
                callperm != PackageManager.PERMISSION_GRANTED ||
                locperm1 != PackageManager.PERMISSION_GRANTED ||
                locperm2 != PackageManager.PERMISSION_GRANTED ) {

            flag++;

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Grant Permissions");
            alertDialog.setMessage("Press OK and select Permissions option to grant all the required permissions.");
            alertDialog.setIcon(R.drawable.dialogicon);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                }


            });

            alertDialog.show();
        }

        int writeperm1 = pm.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, context.getPackageName());
        int readperm1 = pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, context.getPackageName());
        int callperm1 = pm.checkPermission(Manifest.permission.CALL_PHONE, context.getPackageName());
        int locperm11 = pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, context.getPackageName());
        int locperm21 = pm.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, context.getPackageName());

        if(     writeperm1 == PackageManager.PERMISSION_GRANTED &&
                readperm1 == PackageManager.PERMISSION_GRANTED &&
                callperm1 == PackageManager.PERMISSION_GRANTED &&
                locperm11 == PackageManager.PERMISSION_GRANTED &&
                locperm21 == PackageManager.PERMISSION_GRANTED && flag !=0 ){

            flag=0;

            AlertDialog alertDialog1 = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog1.setTitle("Permissions Granted");
            alertDialog1.setMessage("All permissions are granted succesfully !!!");
            alertDialog1.setIcon(R.drawable.dialogcomplete);
            alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                }
            });
            alertDialog1.show();

        }
    }


    public void NotificationFn(){

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mNotifDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications");

        mNotifDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(mCurrentUser.getUid().toString())){

                    mNotifDB = FirebaseDatabase.getInstance().getReference().child("Notifications").child(mCurrentUser.getUid().toString());

                    mNotifDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            sfrom = dataSnapshot.child("from").getValue().toString();

                            mUserDatabse = FirebaseDatabase.getInstance().getReference().child("Users").child(sfrom);

                            mUserDatabse.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    sfull_name = dataSnapshot.child("full_name").getValue().toString();

                                    notification.setSmallIcon(R.drawable.ic_launcher1notif);
                                    notification.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher1));
                                    notification.setTicker("Blood Bank !!!");
                                    notification.setWhen(System.currentTimeMillis());
                                    notification.setContentTitle("New Request from "+ sfull_name);
                                    notification.setContentText("Tap to accept/decline request.");

                                    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    notification.setSound(uri);
                                    //notification.setDefaults(Notification.DEFAULT_SOUND);

                                    //notification.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                                    notification.setDefaults(Notification.DEFAULT_VIBRATE);

                                    Intent i = new Intent(MainActivity.this, NewAcceptActivity.class);
                                    i.putExtra("user_id1", sfrom);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, i,PendingIntent.FLAG_UPDATE_CURRENT);
                                    notification.setContentIntent(pendingIntent);

                                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    nm.notify(uniqueID, notification.build());
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

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

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){

            sendToStart();
        } else{/*

            mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

            mUserDatabase.addValueEventListener(new ValueEventListener() {
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

                        HashMap<String, String > userMap = new HashMap<>();
                        userMap.put("fname", sfname);
                        userMap.put("lname", slname);
                        userMap.put("gender", sgender);
                        userMap.put("age", sage);
                        userMap.put("weight", sweight);
                        userMap.put("blood", sblood1);
                        userMap.put("contact", scontact);
                        userMap.put("address", saddress);
                        userMap.put("city", scity1);
                        userMap.put("state", sstate);
                        userMap.put("image", simage);
                        userMap.put("full_name", sfname + " " + slname);

                        mDonorDB = FirebaseDatabase.getInstance().getReference().child("Donors").child(scity).child(sblood).child(mCurrentUser.getUid());
                        mDonorDB.setValue(userMap);

                        Log.i(TAG, Long.toString(elapsedDays));

                    } else if(elapsedDays <= 90 && int_isdonor == 1){

                        Log.i(TAG, "elsepart");


                        mDonorDB = FirebaseDatabase.getInstance().getReference().child("Donors").child(scity).child(sblood);
                        mDonorDB.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(mCurrentUser.getUid().toString())){

                                    mDonorDBdel = FirebaseDatabase.getInstance().getReference().child("Donors").child(scity).child(sblood).child(mCurrentUser.getUid());
                                    mDonorDBdel.removeValue();
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
            });*/
        }

    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){
            FirebaseAuth.getInstance().signOut();
            sendToStart();
        }

        if(item.getItemId() == R.id.main_settings_btn){
            Intent settingintent = new Intent(MainActivity.this, ContactUsActivity.class);
            startActivity(settingintent);
        }

        if(item.getItemId() == R.id.main_loc){
            Intent share = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(share);
        }

        return true;
    }

    public long Diff(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;

        return elapsedDays;
    }

    private boolean checkWriteExternalPermission()
    {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        //int res = getContext().checkCallingOrSelfPermission(permission);
        //return (res == PackageManager.PERMISSION_GRANTED);
        return true;
    }

}
