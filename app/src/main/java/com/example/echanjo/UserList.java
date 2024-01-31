package com.example.echanjo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserList extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<HelperClass> list;
    DatabaseReference databaseReference;
    MyAdapter adapter;
    SearchView searchView;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UserList.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        recyclerView = findViewById(R.id.recycleview);
        searchView = findViewById(R.id.search);
        searchView.clearFocus();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        list = new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(this, list);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    HelperClass helperClass = dataSnapshot.getValue(HelperClass.class);
                    list.add(helperClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList(newText);
                return false;
            }
        });

    }

    private void searchList(String text) {
        ArrayList<HelperClass> searchList = new ArrayList<>();
        for(HelperClass helperClass:list){
            if(helperClass.getName().toLowerCase().contains(text.toLowerCase())){
                searchList.add(helperClass);
            }
        }
        adapter.searchDataList(searchList);
    }
}