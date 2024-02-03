package com.example.echanjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisteredDoctors extends AppCompatActivity {

    RecyclerView recyclerView;

    DatabaseReference databaseReference;
    SearchView searchView;

    ArrayList<ReadWriteUserDetails>list;
    MainAdapter mainAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_doctors);

        recyclerView = findViewById(R.id.recycleview);
        //searchView = findViewById(R.id.search);
        databaseReference = FirebaseDatabase.getInstance().getReference("Registered Doctors");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainAdapter = new MainAdapter(this,list);
        recyclerView.setAdapter(mainAdapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    ReadWriteUserDetails readWriteUserDetails = dataSnapshot.getValue(ReadWriteUserDetails.class);
                    list.add(readWriteUserDetails);
                }
                mainAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}