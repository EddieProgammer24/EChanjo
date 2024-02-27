package com.example.echanjo;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;
import java.util.Objects;

public class AddChildDetails extends AppCompatActivity {
    Button saveButton;
    EditText uploadFullName, uploadDoB, uploadWeight;
    RadioGroup uploadGender;
    RadioButton radioButtonRegisterGenderSelected;
    FirebaseDatabase firebaseDatabase;
    DatePickerDialog picker;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_child_details);

        uploadFullName = findViewById(R.id.uploadFullName);
        uploadDoB = findViewById(R.id.uploadDoB);
        //RadioButton for Gender
        uploadGender = findViewById(R.id.uploadGender);
        saveButton = findViewById(R.id.saveButton);
        uploadGender.clearCheck();

        uploadWeight = findViewById(R.id.uploadWeight);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Child Details");


        uploadDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker = new DatePickerDialog(AddChildDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        uploadDoB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = uploadGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);
                //getting data from the edittext
                String fullName = uploadFullName.getText().toString();
                String doB = uploadDoB.getText().toString();
                String gender = radioButtonRegisterGenderSelected.getText().toString();
                String weight = uploadWeight.getText().toString();

                if (TextUtils.isEmpty(fullName)) {
                    Toast.makeText(AddChildDetails.this, "Please enter child's full name", Toast.LENGTH_LONG).show();
                    uploadFullName.setError("Full Name is required");
                    uploadFullName.requestFocus();
                } else if (TextUtils.isEmpty(doB)) {
                    Toast.makeText(AddChildDetails.this, "Please select child's Date of Birth", Toast.LENGTH_LONG).show();
                    uploadDoB.setError("Date of Birth is required");
                    uploadDoB.requestFocus();
                } else if (TextUtils.isEmpty(weight)) {
                    Toast.makeText(AddChildDetails.this, "Please enter child's Weight", Toast.LENGTH_LONG).show();
                    uploadDoB.setError("Child's Weight is required");
                    uploadDoB.requestFocus();
                } else {
                    gender = radioButtonRegisterGenderSelected.getText().toString();
                }

                ChildDetails childDetails = new ChildDetails(fullName, doB, gender,weight);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        databaseReference.child(fullName).setValue(childDetails);
                        //display a toast message
                        Toast.makeText(AddChildDetails.this, "Child Details Added...", Toast.LENGTH_SHORT).show();
                        //starting a main activity
                        startActivity(new Intent(AddChildDetails.this,ChildView.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        //display a failure message
                        Toast.makeText(AddChildDetails.this, "Failed to Add Child Details", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

}