package com.manit.amit.bloodbank;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ShareLocationActivity extends AppCompatActivity implements LocationListener {

    public double latitude;
    public double longitude;

    private Button mMakeCall;

    final int REQUEST_PHONE_CALL = 1;

    private DatabaseReference mUsetDatabase;

    public String strnumber;

    public ProgressDialog mRegProgress;

    public DatabaseReference mDonorDB, mCheckLatLong;

    public FirebaseUser mCurrent_user;

    private Toolbar mToolbar;

    public LocationManager locationManager;

    public Criteria criteria;

    public String bestProvider;

    private Button mBtnOnMap, mBtnShowAddress;

    private TextView mAddress;

    public String friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        mRegProgress = new ProgressDialog(this);

        mToolbar = (Toolbar) findViewById(R.id.share_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Share Location");

        mBtnOnMap = findViewById(R.id.btn_onmap);
        mBtnShowAddress = findViewById(R.id.btn_address);

        mMakeCall = findViewById(R.id.makecall);

        mAddress = findViewById(R.id.addresstext);

        mRegProgress.setTitle("Loading Data");
        mRegProgress.setMessage("Please turn on your GPS to Battery/Power Saving Mode to continue !!!");
        mRegProgress.setCanceledOnTouchOutside(false);
        mRegProgress.show();

        mBtnShowAddress.setEnabled(false);
        mBtnOnMap.setEnabled(false);
        mMakeCall.setEnabled(false);

        mBtnOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCheckLatLong = FirebaseDatabase.getInstance().getReference().child("Friends").child(friend);

                mCheckLatLong.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.hasChild("latitude") || !dataSnapshot.hasChild("longitude")) {

                            Toast.makeText(ShareLocationActivity.this, "The user didnt share his location yet !!!", Toast.LENGTH_LONG).show();

                        } else {

                            String lat = dataSnapshot.child("latitude").getValue().toString();
                            String lon = dataSnapshot.child("longitude").getValue().toString();

                            Intent mapintent = new Intent(ShareLocationActivity.this, MapsActivity.class);
                            mapintent.putExtra("latitude", lat);
                            mapintent.putExtra("longitude", lon);
                            startActivity(mapintent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        mBtnShowAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCheckLatLong = FirebaseDatabase.getInstance().getReference().child("Friends").child(friend);

                mCheckLatLong.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.hasChild("latitude") || !dataSnapshot.hasChild("longitude")) {

                            Toast.makeText(ShareLocationActivity.this, "The user didnt share his location yet !!!", Toast.LENGTH_LONG).show();

                        } else {

                            String lat = dataSnapshot.child("latitude").getValue().toString();
                            String lon = dataSnapshot.child("longitude").getValue().toString();

                            latitude = Double.parseDouble(lat);
                            longitude = Double.parseDouble(lon);

                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(ShareLocationActivity.this, Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                if (addresses != null) {

                                    Address returnedAddress = addresses.get(0);

                                    StringBuilder strReturnedAddress = new StringBuilder("");

                                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                                    }
                                    String strAdd = strReturnedAddress.toString();

                                    mAddress.setText(strAdd);

                                    /*String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                    String city = addresses.get(0).getLocality();
                                    String state = addresses.get(0).getAdminArea();
                                    String country = addresses.get(0).getCountryName();
                                    String postalCode = addresses.get(0).getPostalCode();
                                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                                    mAddress.setText(address + "\n" + city + "\n" + state + "\n" + country + "\n" + postalCode + "\n" + knownName);*/

                                } else {

                                    mAddress.setText("No valid Address found !!!");

                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(ShareLocationActivity.this, "No valid Address found !!!", Toast.LENGTH_LONG).show();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        mMakeCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUsetDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(friend);

                mUsetDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        strnumber = dataSnapshot.child("contact").getValue().toString();
                        Intent call = new Intent(Intent.ACTION_CALL);
                        call.setData(Uri.parse("tel:" + strnumber));
                        if (ActivityCompat.checkSelfPermission(ShareLocationActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            ActivityCompat.requestPermissions(ShareLocationActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                            Toast.makeText(ShareLocationActivity.this, "Please give permission to make call", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            startActivity(call);
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        getLocation();

    }

    public static boolean isLocationEnabled(Context context) {

        return true;
    }

    protected void getLocation() {
        if (isLocationEnabled(ShareLocationActivity.this)) {

            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                FetchData(latitude, longitude);

                //Toast.makeText(ShareLocationActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
                Toast.makeText(ShareLocationActivity.this, "Your Location has been updated", Toast.LENGTH_SHORT).show();


            } else {

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling

                    return;
                }
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        } else {
            //prompt user to enable location....
            Toast.makeText(ShareLocationActivity.this, "Enable Your Location to Proceed !!!", Toast.LENGTH_SHORT).show();

        }
    }

    private void FetchData(double latitude, double longitude) {

        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mDonorDB = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user.getUid());

        mDonorDB.child("latitude").setValue(Double.toString(latitude));
        mDonorDB.child("longitude").setValue(Double.toString(longitude));

        mDonorDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("friend")) {

                    mBtnOnMap.setEnabled(true);
                    mBtnShowAddress.setEnabled(true);
                    mMakeCall.setEnabled(true);

                    friend = dataSnapshot.child("friend").getValue().toString();

                } else {

                    Toast.makeText(ShareLocationActivity.this, "You have no one to share the location with !!!", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRegProgress.dismiss();
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);

    }

    @Override
    public void onLocationChanged(Location location) {
        //Hey, a non null location! Sweet!

        locationManager.removeUpdates(this);

        latitude = location.getLatitude();
        longitude = location.getLongitude();

        FetchData(latitude, longitude);

        Toast.makeText(ShareLocationActivity.this, "Your Location has been updated", Toast.LENGTH_SHORT).show();
        //Toast.makeText(ShareLocationActivity.this, "latitude:" + latitude + " longitude:" + longitude, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent call = new Intent(Intent.ACTION_CALL);
                    call.setData(Uri.parse("tel:" + strnumber));
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        return;
                    }
                    startActivity(call);
                }
                else
                {

                }
                return;
            }
        }
    }

}
