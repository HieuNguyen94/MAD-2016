package com.restful_client_android;

import android.content.Context;
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

    public UserAdapter(Context context, ArrayList<User> users){
        this.users = users;
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
        private TextView id;
        private TextView username;
        private TextView email;
        private TextView status;
        private TextView name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();

        View rowView = this.inflater.inflate(R.layout.list_item, null);
        holder.id = (TextView) rowView.findViewById(R.id.id);
        holder.username = (TextView) rowView.findViewById(R.id.username);
        holder.email = (TextView) rowView.findViewById(R.id.email);
        holder.status = (TextView) rowView.findViewById(R.id.status);
        holder.name = (TextView) rowView.findViewById(R.id.name);

        holder.id.setText(String.valueOf(this.users.get(position).getId()));
        holder.username.setText(this.users.get(position).getUsername());
        holder.email.setText(this.users.get(position).getEmail());
        holder.status.setText(String.valueOf(this.users.get(position).getStatus()));
        holder.name.setText(this.users.get(position).getName());

        return rowView;
    }
}
