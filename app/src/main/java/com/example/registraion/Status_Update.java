package com.example.registraion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Status_Update extends AppCompatActivity {
    EditText Message, Phone;
    TextView Save;
    Button Send;

    String Link,Msg;

    FirebaseAuth Auth;
    String UserID;
    DatabaseReference Ref;

    SupportMapFragment TMap;
    FusedLocationProviderClient client;
    LocationManager locationManager;
    LocationRequest locationRequest;
    int LOCATION_REQUEST_CODE = 10001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status__update);

        Auth = FirebaseAuth.getInstance();
        UserID = Auth.getCurrentUser().getUid();
        Ref = FirebaseDatabase.getInstance().getReference("Message").child(UserID);

        Message = findViewById(R.id.Message);
        Phone = findViewById(R.id.Phone);
        Send = findViewById(R.id.Send);
        Save = findViewById(R.id.Save);


        TMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.Map);
        client = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Message.setText(snapshot.child("Message").getValue().toString());
                    Phone.setText(snapshot.child("Phone").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ref = FirebaseDatabase.getInstance().getReference("Message").child(UserID);
                String msg = Message.getText().toString().trim();
                String phone = Phone.getText().toString().trim();

                if (TextUtils.isEmpty(msg) || TextUtils.isEmpty(phone)) {
                    Toast.makeText(Status_Update.this, "Please insert message and phone", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Message", msg);
                    map.put("Phone", phone);
                    Ref.setValue(map);
                    Toast.makeText(Status_Update.this, "Message Added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(Status_Update.this,Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED) {
                    SendText();
                } else {
                    ActivityCompat.requestPermissions(Status_Update.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try{
                checkSettingsAndStartLocationUpdates();
            }catch (Exception e){
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        } else {
            askLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }


    public void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);

        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {

                startLocationUpdates();
            }
        });

    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        client.removeLocationUpdates(locationCallback);
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Please enable location", Toast.LENGTH_SHORT).show();
            }
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingsAndStartLocationUpdates();
            } else {
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }


    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                Toast.makeText(Status_Update.this, "Unable to find location", Toast.LENGTH_SHORT).show();
                return;
            }else{
            for (final Location location : locationResult.getLocations()) {
                TMap.getMapAsync(new OnMapReadyCallback(){
                    @Override
                    public void onMapReady (GoogleMap googleMap){
                        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        googleMap.addMarker(markerOptions).showInfoWindow();
                    }
                });

                Link = "google.com/maps/search/?api=1&query="+location.getLatitude()+","+location.getLongitude();


                }
            }
        }
    };

    public void SendText(){
        Msg = Message.getText().toString();
        Msg = Msg+"\nMy location:\n"+Link;

        String Number = Phone.getText().toString().trim();

        try{
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Number,null,Msg,null,null);
            Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this, "Message sending failed", Toast.LENGTH_SHORT).show();
        }

    }

}