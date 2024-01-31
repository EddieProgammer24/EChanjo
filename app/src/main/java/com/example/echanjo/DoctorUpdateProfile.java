package com.example.echanjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class DoctorUpdateProfile extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDoB, editTextUpdateMobile;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName, textDoB, textGender, textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_update_profile);

        progressBar = findViewById(R.id.progressBar);
        editTextUpdateName = findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB = findViewById(R.id.editText_update_profile_dob);
        editTextUpdateMobile = findViewById(R.id.editText_update_profile_mobile);

        radioGroupUpdateGender = findViewById(R.id.radio_group_update_gender);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Show Profile Data
        showProfile(firebaseUser);

        //Upload Profile Pic
        Button buttonUploadProfilePic = findViewById(R.id.button_upload_profile_pic);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorUpdateProfile.this, DoctorUploadPicture.class);
                startActivity(intent);
                finish();
            }
        });

        //Update Email
        Button buttonUpdateEmail = findViewById(R.id.button_profile_update_email);
        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorUpdateProfile.this, DoctorUpdateEmail.class);
                startActivity(intent);
                finish();
            }
        });

        //Setting up DatePicker on EditText
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Extracting saved dd,mm,yyyy into different variables by creating an array delimited by "/"
                String textSADoB[] = textDoB.split("/");

                int day = Integer.parseInt(textSADoB[0]);
                int month =Integer.parseInt(textSADoB[1])-1; //to take care of month index staring from 0
                int year = Integer.parseInt(textSADoB[2]);

                DatePickerDialog picker;

                //Date Picker Dialog
                picker = new DatePickerDialog(DoctorUpdateProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth + "/" +(month + 1) + "/" + year);
                    }
                },year, month, day);
                picker.show();
            }
        });

        //Update Profile
        Button buttonUpdateProfile = findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });

    }
    //Update Profile

    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID = radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected = findViewById(selectedGenderID);

        if(TextUtils.isEmpty(textFullName)){
            Toast.makeText(DoctorUpdateProfile.this,"Please enter doctor's full name",Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full Name is required");
            editTextUpdateName.requestFocus();
        }  else if (TextUtils.isEmpty(textDoB)) {
            Toast.makeText(DoctorUpdateProfile.this,"Please enter date of birth",Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date of birth is required");
            editTextUpdateDoB.requestFocus();
        } else if (TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())) {
            Toast.makeText(DoctorUpdateProfile.this,"Please select your gender",Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Gender is required");
            radioButtonUpdateGenderSelected.requestFocus();
        } else if (TextUtils.isEmpty(textMobile)) {
            Toast.makeText(DoctorUpdateProfile.this,"Please enter your mobile no",Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No.is required");
            editTextUpdateMobile.requestFocus();
        } else if (textMobile.length() !=10) {
            Toast.makeText(DoctorUpdateProfile.this,"Please re-enter your mobile no",Toast.LENGTH_LONG).show();
            editTextUpdateMobile.setError("Mobile No. should be 10 digits");
            editTextUpdateMobile.requestFocus();
        }  else {
            //Obtain the data entered by the user
            textGender = radioButtonUpdateGenderSelected.getText().toString();
            textFullName = editTextUpdateName.getText().toString();
            textDoB = editTextUpdateDoB.getText().toString();
            textMobile = editTextUpdateMobile.getText().toString();


            //Enter User Data into the Firebase RealTime Database. Set Up Dependencies
            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB,textGender,textMobile);

            //Extract User reference from Database for "Registered Users"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Doctors");

            String userID = firebaseUser.getUid();

            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                        //Setting new Display name
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(textFullName).build();
                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(DoctorUpdateProfile.this,"Update Succeful!",Toast.LENGTH_SHORT).show();

                        //Stop User from returning to the DoctorProfileActivity on pressing back button and close activity
                        Intent intent = new Intent(DoctorUpdateProfile.this,DoctorProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else{
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(DoctorUpdateProfile.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    //Fetch data from firebase and display
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting User Reference from Database for "Registered Doctors"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Doctors");

        progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    textFullName = firebaseUser.getDisplayName();
                    textDoB = readUserDetails.doB;
                    textGender = readUserDetails.gender;
                    textMobile = readUserDetails.mobile;

                    editTextUpdateName.setText(textFullName);
                    editTextUpdateDoB.setText(textDoB);
                    editTextUpdateMobile.setText(textMobile);

                    //Show Gender through Radio Button
                    if(textGender.equals("Male")){
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_male);
                    }else{
                        radioButtonUpdateGenderSelected = findViewById(R.id.radio_female);
                    }
                    radioButtonUpdateGenderSelected.setChecked(true);
                }else{
                    Toast.makeText(DoctorUpdateProfile.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DoctorUpdateProfile.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
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
            Intent intent = new Intent(DoctorUpdateProfile.this,DoctorUpdateProfile.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_email){
            Intent intent = new Intent(DoctorUpdateProfile.this,DoctorUpdateEmail.class);
            startActivity(intent);
        } else if (id == R.id.menu_settings) {
            Toast.makeText(DoctorUpdateProfile.this,"menu_settings",Toast.LENGTH_SHORT).show();
        } else if(id == R.id.menu_change_password){
            Intent intent = new Intent(DoctorUpdateProfile.this,DoctorChangePassword.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile){
            Intent intent = new Intent(DoctorUpdateProfile.this,DoctorDeleteProfile.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(DoctorUpdateProfile.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DoctorUpdateProfile.this,MainActivity.class);

            //Clear stack to prevent user coming back to DoctorProfileActivity on pressing back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //Close DoctorProfileActivity
        }else{
            Toast.makeText(DoctorUpdateProfile.this,"Something wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}