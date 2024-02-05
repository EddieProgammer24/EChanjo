package com.example.echanjo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.ConnectionTelemetryConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ChildAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private Context context;
    private List<DataClass> dataList;

    public ChildAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataList.get(position).getDataImage()).into(holder.recImage);
        holder.recDoB.setText(dataList.get(position).getDoB());
        holder.recFullName.setText(dataList.get(position).getFullName());
        holder.recGender.setText(dataList.get(position).getWeight());
        holder.recWeight.setText(dataList.get(position).getWeight());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChildDetailsActivity.class);
                intent.putExtra("Image",dataList.get(holder.getAdapterPosition()).getDataImage());
                intent.putExtra("DoB",dataList.get(holder.getAdapterPosition()).getDoB());
                intent.putExtra("FullName",dataList.get(holder.getAdapterPosition()).getFullName());
                intent.putExtra("Gender",dataList.get(holder.getAdapterPosition()).getGender());
                intent.putExtra("Weight",dataList.get(holder.getAdapterPosition()).getWeight());
                intent.putExtra("Key",dataList.get(holder.getAdapterPosition()).getKey());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
    public void searchDataList(ArrayList<DataClass> searchList){
        dataList = searchList;
        notifyDataSetChanged();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{
    ImageView recImage;
    TextView recFullName,recDoB,recWeight,recGender;
    CardView recCard;
    public MyViewHolder(View itemView){
        super(itemView);

        recImage = itemView.findViewById(R.id.recImage);
        recCard = itemView.findViewById(R.id.recCard);
        recFullName = itemView.findViewById(R.id.recFullName);
        recDoB = itemView.findViewById(R.id.recDoB);
        recWeight = itemView.findViewById(R.id.recWeight);
        recGender = itemView.findViewById(R.id.recGender);
    }
}