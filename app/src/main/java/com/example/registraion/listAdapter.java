package com.example.registraion;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class listAdapter extends ArrayAdapter {
    private Activity mContext;
    List<setString> ContactList;

    public listAdapter(Activity mContext,List<setString> ContactList){
        super(mContext,R.layout.list_item,ContactList);
        this.mContext = mContext;
        this.ContactList = ContactList;


    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = mContext.getLayoutInflater();
        View listview = inflater.inflate(R.layout.list_item,null,true);

        TextView Name = listview.findViewById(R.id.viewName);
        TextView Phone = listview.findViewById(R.id.viewPhone);
        setString string= ContactList.get(position);

        Name.setText(string.getName());
        Phone.setText(string.getPhone());
        return listview;
    }
}
