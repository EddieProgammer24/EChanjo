package com.example.echanjo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChildView extends AppCompatActivity implements ChildDetailsAdapter.ChildClickInterface {
    private FloatingActionButton addChildFAB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView childRV;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;
    private ArrayList<ChildDetails> childDetailsArrayList;
    private ChildDetailsAdapter childDetailsAdapter;
    private RelativeLayout homeRL;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_view);
        //initializing all our variables
        childRV = findViewById(R.id.idRVChildren);
        loadingPB = findViewById(R.id.idPBLoading);
        addChildFAB = findViewById(R.id.idFABAddChild);
        homeRL = findViewById(R.id.idRLBSheet);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        childDetailsArrayList = new ArrayList<>();
        //on below we are getting database reference
        databaseReference = firebaseDatabase.getReference("Child Details");
        //on below line adding a click listener for our floating action button
        addChildFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opening a new activity for adding a course
                Intent i = new Intent(ChildView.this,AddChildDetails.class);
                startActivity(i);
            }
        });
        //initializing the adapter class
        childDetailsAdapter = new ChildDetailsAdapter(childDetailsArrayList,this,this::onChildClick);
        //setting layout manager to the recycler view on the line below
        childRV.setLayoutManager(new LinearLayoutManager(this));
        //setting adapter to the recycler view
        childRV.setAdapter(childDetailsAdapter);
        //calling a method from the database
        getInfant();

    }

    private void getInfant() {
        //on below line clearing our lists
        childDetailsArrayList.clear();
        //calling add child event listener method to read data
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                //hide progress bar
                loadingPB.setVisibility(View.GONE);
                //adding snapshot to the array list on line below
                childDetailsArrayList.add(snapshot.getValue(ChildDetails.class));
                //notify adapter that data has changed
                childDetailsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // this method is called when new child is added
                // we are notifying our adapter and making progress bar
                // visibility as gone.
                loadingPB.setVisibility(View.GONE);
                childDetailsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // notifying our adapter when child is removed.
                childDetailsAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // notifying our adapter when child is moved.
                childDetailsAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        }

    @Override
    public void onChildClick(int position) {
        displayBottomSheet(childDetailsArrayList.get(position));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void displayBottomSheet(ChildDetails childDetails) {
        // on below line we are creating our bottom sheet dialog.
        final BottomSheetDialog bottomSheetTeachersDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        // on below line we are inflating our layout file for our bottom sheet.
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, homeRL);
        // setting content view for bottom sheet on below line.
        bottomSheetTeachersDialog.setContentView(layout);
        // on below line we are setting a cancelable
        bottomSheetTeachersDialog.setCancelable(false);
        bottomSheetTeachersDialog.setCanceledOnTouchOutside(true);
        // calling a method to display our bottom sheet.
        bottomSheetTeachersDialog.show();
        // on below line we are creating variables for
        // our text view and image view inside bottom sheet
        // and initializing them with their ids.
        TextView FullNameTV = layout.findViewById(R.id.idTVFullName);
        TextView DoBTV = layout.findViewById(R.id.idTVDoB);
        TextView GenderTV = layout.findViewById(R.id.idTVGender);
        TextView WeightTV = layout.findViewById(R.id.idTVWeight);
        //on below line we are setting data to different views on below line
        FullNameTV.setText(childDetails.getFullname());
        DoBTV.setText(childDetails.getDoB());
        GenderTV.setText(childDetails.getGender());
        WeightTV.setText(childDetails.getWeight());
        Button viewBtn = layout.findViewById(R.id.idBtnVIewDetails);
        Button editBtn = layout.findViewById(R.id.idBtnEditDetails);

        //adding an onclick listener for my edit button.
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //on below line we are open an edit child activity
                Intent i = new Intent(ChildView.this,EditChildActivity.class);
                //on below line we are passing the Child Details

                startActivity(i);
            }
        });

        //adding an onclick listener for the view button
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are navigating to a new activity
                //for displaying details
                Intent i = new Intent(Intent.ACTION_VIEW);
                startActivity(i);
            }
        });
    }
}