package com.example.registraion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Emergency_Contact extends AppCompatActivity {
    String root;
    static final int REQUEST_CALL = 1;
    long Temp;
    ListView List;
    Button Police,Hospital,Fire;
    DatabaseReference Ref;
    List<setString> ContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency__contact);

        List = findViewById(R.id.List);
        Police = findViewById(R.id.Police);
        Hospital = findViewById(R.id.Hospital);
        Fire = findViewById(R.id.Fire);


        ContactList = new ArrayList<>();

        Police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ref = FirebaseDatabase.getInstance().getReference("Police Station");
                root = "Police Station";

                Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ContactList.clear();

                        for(DataSnapshot Contact:snapshot.getChildren()){
                            setString string = Contact.getValue(setString.class);
                            ContactList.add(string);
                        }
                        ListAdapter adapter = new listAdapter(Emergency_Contact.this,ContactList);
                        List.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        Hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ref = FirebaseDatabase.getInstance().getReference("Hospital");
                root = "Hospital";

                Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ContactList.clear();

                        for(DataSnapshot Contact:snapshot.getChildren()){
                            setString string = Contact.getValue(setString.class);
                            ContactList.add(string);
                        }
                        ListAdapter adapter = new listAdapter(Emergency_Contact.this,ContactList);
                        List.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        Fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ref = FirebaseDatabase.getInstance().getReference("Fire Service");
                root = "Fire Service";

                Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ContactList.clear();

                        for(DataSnapshot Contact:snapshot.getChildren()){
                            setString string = Contact.getValue(setString.class);
                            ContactList.add(string);
                        }
                        ListAdapter adapter = new listAdapter(Emergency_Contact.this,ContactList);
                        List.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        List.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Temp = position;

                new AlertDialog.Builder(Emergency_Contact.this)
                        .setIcon(android.R.drawable.ic_menu_call)
                        .setMessage("Do you want to call this number?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Ref = FirebaseDatabase.getInstance().getReference(root);

                                Ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        for (DataSnapshot datasnapshot : snapshot.getChildren()){

                                            String Phone = datasnapshot.child("phone").getValue().toString();

                                            if(ContextCompat.checkSelfPermission(Emergency_Contact.this,
                                                    Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                                                ActivityCompat.requestPermissions(Emergency_Contact.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
                                            }else {
                                                Intent intent = new Intent(Intent.ACTION_CALL);
                                                intent.setData(Uri.parse("tel:" + Phone));
                                                startActivity(intent);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        })
                        .setNegativeButton("No",null)
                        .show();

                return true;
            }
        });

    }



}