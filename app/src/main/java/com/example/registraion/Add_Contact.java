package com.example.registraion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Add_Contact extends AppCompatActivity{
    FirebaseAuth Auth;
    String UserID,phn;
    EditText Name,Phone;
    ListView ListView;
    DatabaseReference Ref;
    List<setString>ContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        Auth = FirebaseAuth.getInstance();
        UserID = Auth.getCurrentUser().getUid();
        Ref = FirebaseDatabase.getInstance().getReference("Contact List").child(UserID);

        Name = findViewById(R.id.Name);
        Phone = findViewById(R.id.Phone);
        ListView = findViewById(R.id.List);
        ContactList = new ArrayList<>();

        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ContactList.clear();

                for(DataSnapshot Contact:snapshot.getChildren()){
                    setString string = Contact.getValue(setString.class);
                    ContactList.add(string);
                }
                ListAdapter adapter = new listAdapter(Add_Contact.this,ContactList);
                ListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setString string=(ContactList.get(position));
                phn = string.getPhone();
                Name.setText(string.getName());
                Phone.setText(string.getPhone());

            }
        });

        ListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                setString string=(ContactList.get(position));
                phn = string.getPhone();

                new AlertDialog.Builder(Add_Contact.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setMessage("Do you want to delete this?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Ref = FirebaseDatabase.getInstance().getReference("Contact List").child(UserID);
                                Ref.child(phn).removeValue();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();

                return true;
            }
        });

    }

    public void Save(View view){
        Ref = FirebaseDatabase.getInstance().getReference("Contact List").child(UserID);
        String name = Name.getText().toString().trim();
        String phone = Phone.getText().toString().trim();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please insert name and phone", Toast.LENGTH_SHORT).show();
        }else{
            setString set = new setString(name, phone);
            Ref.child(phone).setValue(set);
            Name.setText("");
            Phone.setText("");
            Toast.makeText(Add_Contact.this, "Contact Added", Toast.LENGTH_SHORT).show();
        }
    }

    public void Update(View view){
        Ref = FirebaseDatabase.getInstance().getReference("Contact List").child(UserID);
        String name = Name.getText().toString().trim();
        String phone = Phone.getText().toString().trim();
        setString set = new setString(name, phone);
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please select a contact", Toast.LENGTH_SHORT).show();
        }else {
            Ref.child(phn).removeValue();
            Ref.child(phone).setValue(set);
            Name.setText("");
            Phone.setText("");
            Toast.makeText(Add_Contact.this, "Contact Updated", Toast.LENGTH_SHORT).show();
        }

    }

}