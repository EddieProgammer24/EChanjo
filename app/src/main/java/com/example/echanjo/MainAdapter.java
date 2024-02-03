package com.example.echanjo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    Context context;
    ArrayList<ReadWriteUserDetails>list;

    public MainAdapter(Context context,ArrayList<ReadWriteUserDetails>list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MainAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.doctorentry,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MyViewHolder holder, int position) {
        ReadWriteUserDetails readWriteUserDetails = list.get(position);
        holder.fullname.setText(readWriteUserDetails.getFullname());
        holder.gender.setText(readWriteUserDetails.getGender());
        holder.dob.setText(readWriteUserDetails.getDoB());
        holder.mobile.setText(readWriteUserDetails.getMobile());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView fullname,gender,dob,mobile;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.textname);
            gender= itemView.findViewById(R.id.textgender);
            dob = itemView.findViewById(R.id.textdob);
            mobile = itemView.findViewById(R.id.textmobile);
        }
    }
}
