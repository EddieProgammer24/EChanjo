package com.example.echanjo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DoctorDetails extends AppCompatActivity {

    TextView detailDesc,detailTitle,detailLang;
    ImageView detailImage;
    FloatingActionButton deleteButton,editButton;
    String key ="";
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_details);

        detailDesc = findViewById(R.id.detailDesc);
        detailImage = findViewById(R.id.detailImage);
        detailTitle = findViewById(R.id.detailTitle);
        deleteButton = findViewById(R.id.deleteButton);
        editButton = findViewById(R.id.editButton);
        detailLang = findViewById(R.id.detailLang);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            detailDesc.setText(bundle.getString("Description"));
            detailTitle.setText(bundle.getString("Title"));
            detailLang.setText(bundle.getString("Language"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Doctor Details");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(DoctorDetails.this, "Deleted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),TestActivity.class));
                        finish();
                    }
                });
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DoctorDetails.this,UpdateActivity.class)
                        .putExtra("Title",detailTitle.getText().toString())
                        .putExtra("Description",detailDesc.getText().toString())
                        .putExtra("Language",detailLang.getText().toString())
                        .putExtra("Image",imageUrl)
                        .putExtra("Key",key);
                startActivity(intent);
            }
        });
    }
}