package com.example.echanjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class DoctorProfileActivity extends AppCompatActivity {
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private String fullName,email,doB,gender,mobile;
    private ImageView imageView;
    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewFullName = findViewById(R.id.textView_show_full_name);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewDoB = findViewById(R.id.textView_show_dob);
        textViewGender = findViewById(R.id.textView_show_gender);
        textViewMobile = findViewById(R.id.textView_show_mobile);
        progressBar = findViewById(R.id.progressBar);

        //Set OnClickListener on ImageView to Open UploadProfilePicActivity
        imageView = findViewById(R.id.imageView_profile_dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorProfileActivity.this,DoctorUploadPicture.class);
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(DoctorProfileActivity.this,
                    "Something went wrong! User's details are not available at the moment",Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting User Reference from Database for "Registered Doctors"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Doctors");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    fullName = firebaseUser.getDisplayName();
                    email = firebaseUser.getEmail();
                    doB = readUserDetails.doB;
                    gender = readUserDetails.gender;
                    mobile = readUserDetails.mobile;

                    textViewWelcome.setText("Welcome, " + fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    textViewMobile.setText(mobile);

                    //Set User DP (After user has uploaded)
                    Uri uri = firebaseUser.getPhotoUrl();
                    Picasso.with(DoctorProfileActivity.this).load(uri).into(imageView);
                } else {
                    Toast.makeText(DoctorProfileActivity.this,
                            "Something went wrong!",Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorProfileActivity.this,
                        "Something went wrong!",Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    //Creating ActionBar Menu


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //When any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.menu_refresh){
            //RefreshActivity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }else if(id == R.id.menu_update_profile){
            Intent intent = new Intent(DoctorProfileActivity.this,DoctorUpdateProfile.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_email){
            Intent intent = new Intent(DoctorProfileActivity.this,DoctorUpdateEmail.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_settings) {
            Toast.makeText(DoctorProfileActivity.this,"menu_settings",Toast.LENGTH_SHORT).show();
        } else if(id == R.id.menu_change_password){
            Intent intent = new Intent(DoctorProfileActivity.this,DoctorChangePassword.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile){
            Intent intent = new Intent(DoctorProfileActivity.this,DoctorDeleteProfile.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(DoctorProfileActivity.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DoctorProfileActivity.this,MainActivity.class);

            //Clear stack to prevent user coming back to DoctorProfileActivity on pressing back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //Close DoctorProfileActivity
        }else{
            Toast.makeText(DoctorProfileActivity.this,"Something wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}