package com.manit.amit.bloodbank;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class SettingActivity extends AppCompatActivity {

    private ProgressDialog mProgress, mProgressDialog;

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private CircleImageView mDisplayImage;
    private TextView mName, mblood, mcontact, maddress;

    private Button mchangedetails, mchangeimage;

    private static final int GALLERY_PICK = 1;

    //Storage Firebase
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Loading Data");
        mProgress.setMessage("Please wait while we fetch your Account Details");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_name);
        mblood = (TextView) findViewById(R.id.settings_blood);
        mcontact = (TextView) findViewById(R.id.settings_contact);
        maddress = (TextView) findViewById(R.id.settings_address);

        mchangedetails = (Button) findViewById(R.id.setting_change_details);
        mchangeimage = (Button) findViewById(R.id.setting_change_image);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mImageStorage = FirebaseStorage.getInstance().getReference();

        String current_uid = mCurrentUser.getUid().toString();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String sfname = dataSnapshot.child("fname").getValue().toString();
                String slname = dataSnapshot.child("lname").getValue().toString();
                String sblood = dataSnapshot.child("blood").getValue().toString();
                String scontact = dataSnapshot.child("contact").getValue().toString();
                String saddress = dataSnapshot.child("address").getValue().toString();
                String scity = dataSnapshot.child("city").getValue().toString();
                String sstate = dataSnapshot.child("state").getValue().toString();
                String simage = dataSnapshot.child("image").getValue().toString();

                String fullname = sfname + " " + slname;
                String fulladdress = saddress + " , " + scity + " , " + sstate;
                String sfullblood = "Blood Type : " + sblood;

                mName.setText(fullname);
                mblood.setText(sfullblood);
                mcontact.setText("Phone : " + scontact);
                maddress.setText(fulladdress);

                if(!simage.equals("default_pic")) {

                    Picasso.with(SettingActivity.this).load(simage).placeholder(R.drawable.developer).into(mDisplayImage);
                }

                if(mName.getText() == fullname){
                    mProgress.dismiss();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mchangedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settiings = new Intent(SettingActivity.this, ChangedetailsActivity.class);
                startActivity(settiings);
                finish();
            }
        });

        mchangeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog = new ProgressDialog(SettingActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait while we upload the image.");
                mProgressDialog.setCanceledOnTouchOutside(true);
                mProgressDialog.show();

                Uri resultUri = result.getUri();

                File thumb_filePath = new File(resultUri.getPath());

                String current_user_id = mCurrentUser.getUid();

                Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference filepath = mImageStorage.child("profile_images").child(current_user_id + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(current_user_id + ".jpg");


                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask  = thumb_filepath.putBytes(thumb_byte);

                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_DownloadUrl = thumb_task.getResult().getDownloadUrl().toString();

                                    if(thumb_task.isSuccessful()){

                                        Map update_hashmap = new HashMap<>();
                                        update_hashmap.put("image", download_url);
                                        update_hashmap.put("thumb_image", thumb_DownloadUrl);

                                        mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingActivity.this, "Success Uploading", Toast.LENGTH_LONG).show();

                                                }
                                            }
                                        });

                                    } else{

                                        mProgressDialog.dismiss();
                                        Toast.makeText(SettingActivity.this, "Error in Uploading ThumbNail !!!", Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                        } else{

                            mProgressDialog.dismiss();
                            Toast.makeText(SettingActivity.this, "Error in Uploading Image !!!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }


    }

}
