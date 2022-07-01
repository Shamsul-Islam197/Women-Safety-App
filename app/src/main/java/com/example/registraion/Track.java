package com.example.registraion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Track extends AppCompatActivity implements OnMapReadyCallback{
    FirebaseAuth Auth;
    String UserID,Phn,Latitude,Longitude;
    EditText Phone;
    DatabaseReference Ref;
    Button Track;
    Double Lat,Lng;

    SupportMapFragment TMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        TMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Map);


        Auth = FirebaseAuth.getInstance();
        UserID = Auth.getCurrentUser().getUid();

        Phone = findViewById(R.id.Phone);
        Track = findViewById(R.id.Track);
        Phone = findViewById(R.id.Phone);



        Track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Phn = Phone.getText().toString().trim();
                if(TextUtils.isEmpty(Phn)){
                    Toast.makeText(Track.this, "Please insert a number", Toast.LENGTH_SHORT).show();
                }else {

                        Ref = FirebaseDatabase.getInstance().getReference("User Location").child(Phn);
                        Ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    Latitude = (snapshot.child("Latitude").getValue().toString());
                                    Longitude = (snapshot.child("Longitude").getValue().toString());
                                        Map();
                                }

                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                }
            }
        });



    }

    public void Map(){
        TMap.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
            Lat = Double.parseDouble(Latitude);
            Lng = Double.parseDouble(Longitude);
            LatLng latLng = new LatLng(Lat,Lng);
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You friend is here");
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            googleMap.addMarker(markerOptions).showInfoWindow();}

}