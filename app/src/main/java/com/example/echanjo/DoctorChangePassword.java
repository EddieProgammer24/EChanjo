package com.example.echanjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DoctorChangePassword extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew, getEditTextPwdConfirmNew;
    private TextView textViewAuthenticated;
    private Button buttonChangePwd, buttonReAuthenticate;
    private ProgressBar progressBar;
    private String userPwdCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_change_password);

        getSupportActionBar().setTitle("Change Password");

        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr = findViewById(R.id.editText_change_pwd_current);
        getEditTextPwdConfirmNew = findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd = findViewById(R.id.button_change_pwd);

        //Disable editText for NewPassword, Confirm New Password and make Change Pwd Button
        editTextPwdNew.setEnabled(false);
        getEditTextPwdConfirmNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(DoctorChangePassword.this,"Something went wrong! User's details not available",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DoctorChangePassword.this,DoctorProfileActivity.class);
            startActivity(intent);
            finish();
        }else{
            reAuthenticateUser(firebaseUser);
        }
    }

    //ReAuthenticate User before changing password

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr = editTextPwdCurr.getText().toString();

                if(TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(DoctorChangePassword.this,"Password is needed",Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("Please enter your current password to authenticate");
                    editTextPwdCurr.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    //ReAuthenticate user now
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.VISIBLE);

                                //Disable editText for current password. Enable EditText for New Password and confirm new password
                                editTextPwdCurr.setEnabled(false);
                                editTextPwdNew.setEnabled(true);
                                getEditTextPwdConfirmNew.setEnabled(true);

                                //Enable Change Pwd Button. Disable Authenticate Button
                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePwd.setEnabled(true);

                                //Set TextView to show User is authenticated/verified
                                textViewAuthenticated.setText("You are authenticated/verified." +
                                        "You can change password now!");
                                Toast.makeText(DoctorChangePassword.this,"Password has been verified." +
                                        "Change Password now",Toast.LENGTH_SHORT).show();

                                //Update color of Change Password Button
                                buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(
                                        DoctorChangePassword.this,R.color.dark_green));

                                buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePwd(firebaseUser);
                                    }
                                });
                            }else{
                                try{
                                    task.getException();
                                } catch (Exception e){
                                    Toast.makeText(DoctorChangePassword.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew = getEditTextPwdConfirmNew.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(DoctorChangePassword.this,"New Password is needed",Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter your new password");
            editTextPwdNew.requestFocus();
        } else if (TextUtils.isEmpty(userPwdConfirmNew)) {
            Toast.makeText(DoctorChangePassword.this,"Please confirm your new password",Toast.LENGTH_SHORT).show();
            getEditTextPwdConfirmNew.setError("Please re-enter your new password");
            getEditTextPwdConfirmNew.requestFocus();
        } else if (!userPwdNew.matches(userPwdConfirmNew)) {
            Toast.makeText(DoctorChangePassword.this,"Password did not match",Toast.LENGTH_SHORT).show();
            getEditTextPwdConfirmNew.setError("Please re-enter same password");
            getEditTextPwdConfirmNew.requestFocus();
        } else if (!userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(DoctorChangePassword.this,"New Password cannot be same as old password",Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter a new password");
            editTextPwdNew.requestFocus();
        }else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(DoctorChangePassword.this,"Password has been changed",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DoctorChangePassword.this,DoctorProfileActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        try {
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(DoctorChangePassword.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
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
            Intent intent = new Intent(DoctorChangePassword.this,DoctorUpdateProfile.class);
            startActivity(intent);
            finish();
        }else if(id == R.id.menu_update_email){
            Intent intent = new Intent(DoctorChangePassword.this,DoctorUpdateEmail.class);
            startActivity(intent);
        } else if (id == R.id.menu_settings) {
            Toast.makeText(DoctorChangePassword.this,"menu_settings",Toast.LENGTH_SHORT).show();
        } else if(id == R.id.menu_change_password){
            Intent intent = new Intent(DoctorChangePassword.this,DoctorChangePassword.class);
            startActivity(intent);
        } else if (id == R.id.menu_delete_profile){
            Intent intent = new Intent(DoctorChangePassword.this,DoctorDeleteProfile.class);
            startActivity(intent);
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(DoctorChangePassword.this,"Logged Out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DoctorChangePassword.this,MainActivity.class);

            //Clear stack to prevent user coming back to DoctorProfileActivity on pressing back button after Logging out
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //Close DoctorProfileActivity
        }else{
            Toast.makeText(DoctorChangePassword.this,"Something wrong!",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}