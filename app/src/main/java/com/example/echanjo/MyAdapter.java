package com.example.echanjo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<HelperClass> list;

    public MyAdapter(Context context, ArrayList<HelperClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.userentry,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HelperClass helperClass = list.get(position);
        holder.name.setText(helperClass.getName());
        holder.email.setText(helperClass.getEmail());
        holder.password.setText(helperClass.getPassword());
        holder.username.setText(helperClass.getUsername());
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.itemView.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_popup))
                        .setExpanded(true,1200)
                        .create();

                //dialog Show()
                View view = dialogPlus.getHolderView();

                EditText name = view.findViewById(R.id.edtName);
                EditText email = view.findViewById(R.id.edtEmail);
                EditText username = view.findViewById(R.id.edtUsername);
                EditText password = view.findViewById(R.id.edtPassword);

                Button btnUpdate = view.findViewById(R.id.btnUpdate);

                name.setText(helperClass.getName());
                email.setText(helperClass.getEmail());
                username.setText(helperClass.getUsername());
                password.setText(helperClass.getPassword());

                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map = new HashMap<>();
                        map.put("name",name.getText().toString());
                        map.put("email",email.getText().toString());
                        map.put("username",username.getText().toString());
                        map.put("password",password.getText().toString());

                    }
                });
            }
        });

    }

    private int getRef(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void searchDataList(ArrayList<HelperClass> searchList)
    {
        list = searchList;
        notifyDataSetChanged();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name,email,username,password;
        Button btnEdit,btnDelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textname);
            email = itemView.findViewById(R.id.textemail);
            password = itemView.findViewById(R.id.textpassword);
            username = itemView.findViewById(R.id.textusername);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }
}