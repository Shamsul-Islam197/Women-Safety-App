package com.example.registraion;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class DashBoard extends AppCompatActivity {
    ImageView Profile,AddContact,Emergency,Status,Panic,TrackFrnd;
    Button SignOut,Control;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        Profile = findViewById(R.id.Profile);
        AddContact = findViewById(R.id.AddContact);
        SignOut = findViewById(R.id.SignOut);
        Emergency = findViewById(R.id.Emergency);
        Status = findViewById(R.id.Status);
        Panic = findViewById(R.id.Panic);
        Control = findViewById(R.id.Control);
        TrackFrnd = findViewById(R.id.Track);


        final Intent intent = new Intent(this,MyService.class);

        Control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Control.getText()=="Start") {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                    Control.setText("Stop");
                    Control.setTextColor(Color.RED);
                }else{
                    stopService(intent);
                    Control.setText("Start");
                    Control.setTextColor(Color.BLACK);
                }
            }
        });



        Panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),PanicMode.class));
            }
        });

        Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class));
            }
        });

        AddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Add_Contact.class));
            }
        });

        Emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Emergency_Contact.class));
            }
        });

        Status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Status_Update.class));
            }
        });

        TrackFrnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Track.class));
            }
        });



        SignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DashBoard.this);
                dialog.setTitle("Are you sure?");

                dialog.setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }
}