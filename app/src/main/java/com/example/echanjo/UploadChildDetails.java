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

import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Objects;

public class UploadChildDetails extends AppCompatActivity {

    ImageView uploadImage;
    Button saveButton;
    EditText uploadFullName, uploadDoB, uploadWeight;
    RadioGroup uploadGender;
    RadioButton radioButtonRegisterGenderSelected;
    String imageURL;
    Uri uri;

    DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_child_details);

        uploadImage = findViewById(R.id.uploadImage);
        uploadFullName = findViewById(R.id.uploadFullName);
        uploadDoB = findViewById(R.id.uploadDoB);
        //RadioButton for Gender
       uploadGender= findViewById(R.id.uploadGender);
       uploadGender.clearCheck();

        uploadWeight = findViewById(R.id.uploadWeight);
        saveButton = findViewById(R.id.saveButton);

        uploadDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker = new DatePickerDialog(UploadChildDetails.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        uploadDoB.setText(dayOfMonth + "/" +(month + 1) + "/" + year);
                    }
                },year,month,day);
                picker.show();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        }else{
                            Toast.makeText(UploadChildDetails.this,"No Image Selected",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });
    }

    public void saveData(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Child Images")
                .child(Objects.requireNonNull(uri.getLastPathSegment()));

        AlertDialog.Builder builder = new AlertDialog.Builder(UploadChildDetails.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }
    public void uploadData(){
        int selectedGenderId = uploadGender.getCheckedRadioButtonId();
        radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

        String fullName = uploadFullName.getText().toString();
        String doB = uploadDoB.getText().toString();
        String gender = radioButtonRegisterGenderSelected.getText().toString();
        String weight = uploadWeight.getText().toString();

        if(TextUtils.isEmpty(fullName)){
            Toast.makeText(UploadChildDetails.this,"Please enter child's full name",Toast.LENGTH_LONG).show();
            uploadFullName.setError("Full Name is required");
            uploadFullName.requestFocus();
        } else if (TextUtils.isEmpty(doB)) {
            Toast.makeText(UploadChildDetails.this,"Please select child's Date of Birth",Toast.LENGTH_LONG).show();
            uploadDoB.setError("Date of Birth is required");
            uploadDoB.requestFocus();
        } else if (TextUtils.isEmpty(weight)) {
            Toast.makeText(UploadChildDetails.this,"Please enter child's Weight",Toast.LENGTH_LONG).show();
            uploadDoB.setError("Child's Weight is required");
            uploadDoB.requestFocus();
        }else {
            gender = radioButtonRegisterGenderSelected.getText().toString();
        }

        //DataClass dataClass = new DataClass(fullName,doB,gender,weight,imageURL);
        //We are changing the child from title to currentDate
        //because we will be updating title as well and it may affect child value

        //String currentDate = DateFormat.getDateInstance().format(Calendar.getInstance().getTime());


       /*FirebaseDatabase.getInstance().getReference("Child Details")

                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UploadChildDetails.this,"Saved",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UploadChildDetails.this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();
                    }
                });*/
    }
}