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
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanicMode extends AppCompatActivity {
    EditText Message;
    TextView Save;
    Button Panic;

    FirebaseAuth Auth;
    String UserID,Msg,Link,Phone;

    List<String> Num= new ArrayList<>();

    DatabaseReference Ref,Ref2,Ref3,Ref4;

    FusedLocationProviderClient client;
    LocationManager locationManager;
    LocationRequest locationRequest;
    int LOCATION_REQUEST_CODE = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panic_mode);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        client = LocationServices.getFusedLocationProviderClient(this);


        Auth = FirebaseAuth.getInstance();
        UserID = Auth.getCurrentUser().getUid();
        Ref = FirebaseDatabase.getInstance().getReference("Message").child(UserID).child("Panic");

        Message = findViewById(R.id.Message);
        Save = findViewById(R.id.Save);
        Panic = findViewById(R.id.Panic);

        getNumber();



        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Message.setText(snapshot.child("Emergency").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = Message.getText().toString().trim();

                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(PanicMode.this, "Please insert a message", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("Emergency", msg);
                    Ref.setValue(map);
                    Toast.makeText(PanicMode.this, "Message Added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Panic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Panic.getText()=="Start"){
                    Panic.setText("Stop");
                }else{
                    Panic.setText("Start");
                }
            }
        });

        Ref4 = FirebaseDatabase.getInstance().getReference("Profile Info").child(UserID);

        Ref4.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Phone = (snapshot.child("Phone").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    public void getNumber(){
        Ref2 = FirebaseDatabase.getInstance().getReference("Contact List").child(UserID);
        Ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot Number:snapshot.getChildren()){
                        Num.add(Number.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    Toast.makeText(PanicMode.this, "Unable to find location", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    for (final Location location : locationResult.getLocations()) {
                        Link = "google.com/maps/search/?api=1&query=" + location.getLatitude() + "," + location.getLongitude();
                        if (Panic.getText() == "Stop") {
                            sendText();
                        }

                        Ref3= FirebaseDatabase.getInstance().getReference("User Location");
                        Map<String, Object> map = new HashMap<>();
                        map.put("Latitude", location.getLatitude());
                        map.put("Longitude", location.getLongitude());
                        if(TextUtils.isEmpty(Phone)){
                            Log.d("Tag","No number fetched");
                        }else{
                            Ref3.child(Phone).setValue(map);
                        }


                    }
                }
            }
        };


    public void sendText(){
        Msg = Message.getText().toString();
        Msg = Msg+"\nMy location:\n"+Link;

        if(ContextCompat.checkSelfPermission(PanicMode.this, Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_GRANTED) {
            for(String list:Num) {
                Log.d("TAG","Number:" +list);
                if (Msg != "" || list != "") {

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(list, null, Msg, null, null);
                        Log.d("Tag","Message Sent");
                        Toast.makeText(PanicMode.this, "Message Sent", Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(PanicMode.this, "Message sending failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        } else {
            ActivityCompat.requestPermissions(PanicMode.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
    }

}