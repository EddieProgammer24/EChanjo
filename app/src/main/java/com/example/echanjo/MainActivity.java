package com.example.echanjo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    ImageView imageSign,imageHome,imageAppointment,imageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imageSign = findViewById(R.id.imgSignup);

        imageSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,user_login.class);
                startActivity(intent);
            }
        });

        imageHome = findViewById(R.id.imgHome);
        imageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserList.class);
                startActivity(intent);
            }
        });

        imageAppointment = findViewById(R.id.imgAppointment);
        imageAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DoctorRegisterActivity.class);
                startActivity(intent);
            }
        });

        imageInfo = findViewById(R.id.imgInfo);

        imageInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DoctorMainLogin.class);
                startActivity(intent);
            }
        });
    }
}