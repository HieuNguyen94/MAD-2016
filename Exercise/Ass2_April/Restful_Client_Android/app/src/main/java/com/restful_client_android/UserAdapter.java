package com.restful_client_android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by TRONGNGHIA on 3/27/2016.
 */
public class UserAdapter extends BaseAdapter{

    private ArrayList<User> users;
    private LayoutInflater inflater;
    private Context context;

    public UserAdapter(Context context, ArrayList<User> users){
        this.users = users;
        this.context = context;
        Log.e("Users size", String.valueOf(this.users.size()));
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class Holder{
        private TextView username;
        private TextView email;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();

        View rowView = null;

        rowView = this.inflater.inflate(R.layout.list_item, null);

        final String id = String.valueOf(this.users.get(position).getId());
        final String username = this.users.get(position).getUsername();
        final String email = this.users.get(position).getEmail();
        final String priority = this.users.get(position).getStatus();
        final String name = this.users.get(position).getName();

        holder.username = (TextView) rowView.findViewById(R.id.username);
        holder.email = (TextView) rowView.findViewById(R.id.email);

        holder.username.setText(username);
        holder.email.setText(email);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDetailActivity.class);
                Bundle userDetail = new Bundle();
                userDetail.putString("id", id);
                userDetail.putString("username", username);
                userDetail.putString("email", email);
                userDetail.putString("priority", priority);
                userDetail.putString("name", name);
                intent.putExtra("DETAIL", userDetail);
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}
