package com.example.echanjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DoctorUpdateEmail extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_update_email);

        progressBar = findViewById(R.id.progressBar);
        editTextPwd = findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticated = findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail = findViewById(R.id.button_update_email);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        //Set Old Email ID on TextView
        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if(firebaseUser.equals("")){
            Toast.makeText(DoctorUpdateEmail.this,"Something went wrong! User's details not available",Toast.LENGTH_LONG).show();
        }else{
            reAuthenticate(firebaseUser);
        }

    }

    //ReAuthenticate/verify User before updating email
    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtain Password for Authentication
                userPwd = editTextPwd.getText().toString();

                if(TextUtils.isEmpty(userPwd)){
                    Toast.makeText(DoctorUpdateEmail.this,"Password is needed to continue",Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("Please enter your password for authentication");
                    editTextPwd.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(DoctorUpdateEmail.this, "Password has been verified." +
                                        "You can update email now.", Toast.LENGTH_SHORT).show();

                                //Set TextView to show that user is authenticated
                                textViewAuthenticated.setText("You are authenticated. You can update your email now.");

                                //Disable EditText for password and enable EditText for new Email and Update Email button
                                editTextNewEmail.setEnabled(true);
                                editTextPwd.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                //Change color of Update Email button
                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(DoctorUpdateEmail.this,
                                        R.color.dark_green));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail = editTextNewEmail.getText().toString();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            Toast.makeText(DoctorUpdateEmail.this, "New Email is required", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please enter new Email");
                                            editTextNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(DoctorUpdateEmail.this, "Please enter valid Email", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please provide valid Email");
                                            editTextNewEmail.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(DoctorUpdateEmail.this, "New Email cannot be same as old Email", Toast.LENGTH_SHORT).show();
                                            editTextNewEmail.setError("Please provide valid Email");
                                            editTextNewEmail.requestFocus();
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            updateEmail(firebaseUser);
                                        }
                                    }
                                });

                            }else{
                                try {
                                    throw task.getException();
                                }catch (Exception e){
                                    Toast.makeText(DoctorUpdateEmail.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                }
            }
        });

    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){

                    //Verify Email
                    firebaseUser.sendEmailVerification();

                    Toast.makeText(DoctorUpdateEmail.this,"Email has been updated. Please verify your new Email",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DoctorUpdateEmail.this,DoctorProfileActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(DoctorUpdateEmail.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
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
            Intent intent = new Intent(DoctorUpdateEmail.this,DoctorUpdateProfile.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_email){
            Intent intent = new Intent(DoctorUpdateEmail.this,DoctorUpdateEmail.class);
            startActivity(intent);
        } else if (id == R.id.menu_settings) {
            Toast.makeText(DoctorUpdateEmail.this,"menu_settings",Toast.LENGTH_SHORT).show();
        } else if(id == R.id.menu_change_password){
            Intent intent = new Intent(DoctorUpdateEmail.this,DoctorChangePassword.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile){
            Intent intent = new Intent(DoctorUpdateEmail.this,DoctorDeleteProfile.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(DoctorUpdateEmail.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DoctorUpdateEmail.this,MainActivity.class);

            //Clear stack to prevent user coming back to DoctorProfileActivity on pressing back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //Close DoctorProfileActivity
        }else{
            Toast.makeText(DoctorUpdateEmail.this,"Something wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
