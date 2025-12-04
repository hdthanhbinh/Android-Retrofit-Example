package com.example.retrofitexample.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.retrofitexample.R;
import com.example.retrofitexample.activities.UserEditActivity;
import com.example.retrofitexample.model.User;
import com.squareup.picasso.Picasso;

import java.util.List;
//implements AdapterView.OnItemClickListener
public class CustomListViewAdapter extends ArrayAdapter<User> {
    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;
    private final List<User> users;

    public CustomListViewAdapter(Context context, List<User> users) {
        super(context, 0, users);
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return users.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_view_item, null);

            holder = new ViewHolder();
            holder.userImage = (ImageView) convertView.findViewById(R.id.user_image);
            holder.userNameLabel = (TextView) convertView.findViewById(R.id.user_name_label);
            holder.userAddressLabel = (TextView) convertView.findViewById(R.id.user_address_label);
            convertView.setTag(holder);

        } else {
            //Get viewholder we already created
            holder = (ViewHolder) convertView.getTag();
        }

        User user = users.get(position);
        if (user != null) {
            String avatar = user.getAvatar();
            if (avatar == null || avatar.isEmpty()) {
                // use built-in placeholder if no avatar URL
                holder.userImage.setImageResource(android.R.drawable.ic_menu_report_image);
            } else {
                Picasso.get()
                        .load(avatar)
                        .placeholder(android.R.drawable.ic_menu_report_image)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(holder.userImage);
            }
            holder.userNameLabel.setText(user.getName() != null ? user.getName() : "(no name)");
            holder.userAddressLabel.setText("ID: " + user.getId());

        }
        return convertView;
    }


    private static class ViewHolder {
        TextView userNameLabel;
        TextView userAddressLabel;
        ImageView userImage;
    }


}
