package com.example.echanjo;



import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Region;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Pattern;

public class DoctorRegisterActivity extends AppCompatActivity {
    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB, editTextRegisterMobile,
            editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private DatePickerDialog picker;
    private static final String TAG="DoctorRegisterActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        Toast.makeText(DoctorRegisterActivity.this,"You can register now",Toast.LENGTH_LONG).show();

        progressBar = findViewById(R.id.progressBar);
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);

        //RadioButton for Gender
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();

        //Setting up DatePicker on EditText
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker = new DatePickerDialog(DoctorRegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" +(month + 1) + "/" + year);
                    }
                },year, month, day);
                picker.show();
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                //Obtain the entered data
                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegisterEmail.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textPwd = editTextRegisterPwd.getText().toString();
                String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();
                String textGender; //Can't obtain the value before verifying if any button was selected or not

                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(DoctorRegisterActivity.this,"Please enter doctor's full name",Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(DoctorRegisterActivity.this,"Please enter doctor's email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(DoctorRegisterActivity.this,"Please re-enter your email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Valid email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(DoctorRegisterActivity.this,"Please enter date of birth",Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date of birth is required");
                    editTextRegisterDoB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(DoctorRegisterActivity.this,"Please select your gender",Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(DoctorRegisterActivity.this,"Please enter your mobile no",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile No.is required");
                    editTextRegisterMobile.requestFocus();
                } else if (textMobile.length() !=10) {
                    Toast.makeText(DoctorRegisterActivity.this,"Please re-enter your mobile no",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile No. should be 10 digits");
                    editTextRegisterMobile.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(DoctorRegisterActivity.this,"Please enter your password",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is required");
                    editTextRegisterPwd.requestFocus();
                } else if (textPwd.length() < 6) {
                    Toast.makeText(DoctorRegisterActivity.this,"Password should be at least 6 digits",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("Password is too weak");
                    editTextRegisterPwd.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPwd)){
                    Toast.makeText(DoctorRegisterActivity.this,"Please confirm your password",Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)){
                    Toast.makeText(DoctorRegisterActivity.this,"Please enter your email", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("Password confirmation is required");
                    editTextRegisterConfirmPwd.requestFocus();
                    //Clear the entered passwords
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textGender, textMobile, textPwd);
                }
            }
        });

    }

    //Register User using the credentials given
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //Create User Profile
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(DoctorRegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            //Update Display name of User
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            //Save User's Data into Firebase Realtime Database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName,textDoB,textGender,textMobile);

                            //Extracting user reference from Database for "Registered Users"
                            DatabaseReference referenceProfile  = FirebaseDatabase.getInstance().getReference("Registered Doctors");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //Send Verification Email
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(DoctorRegisterActivity.this, "User registered successfully. Please verify your email", Toast.LENGTH_LONG).show();
                                        //Open User Profile after a succesful registration
                                        Intent intent = new Intent(DoctorRegisterActivity.this,DoctorProfileActivity.class);
                                        //To prevent user from returning to register activity on pressing back button after registration.
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish(); //to close DoctorRegisterActivity
                                    }else{
                                        Toast.makeText(DoctorRegisterActivity.this, "User registration failed. Please verify your email",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    //Hide ProgressBar whether User creation is successful or not
                                    progressBar.setVisibility(View.GONE);

                                }
                            });


                        } else {
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException  e){
                                editTextRegisterPwd.setError("Your password is too weak. Kindly use a mix of alphabets, numbers, and special characters");
                                editTextRegisterPwd.requestFocus();
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                editTextRegisterPwd.setError("Your email is invalid or already in use. Kindly re-enter.");
                                editTextRegisterPwd.requestFocus();
                            }catch (FirebaseAuthUserCollisionException e){
                                editTextRegisterPwd.setError("User is already registered with this email. Use another email.");
                                editTextRegisterPwd.requestFocus();
                            }catch (Exception e){
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(DoctorRegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}