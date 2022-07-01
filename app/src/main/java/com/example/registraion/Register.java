package com.example.registraion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    EditText FirstName,LastName,Email,Phone,Password,Password2;
    Button Register;
    TextView Login;
    FirebaseAuth Auth;
    ProgressBar ProgressBar;
    FirebaseFirestore Fstore;
    DatabaseReference Ref;
    String UserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        FirstName = findViewById(R.id.editTextFirstName);
        LastName = findViewById(R.id.editTextLastName);
        Email = findViewById(R.id.editTextEmail);
        Phone = findViewById(R.id.editTextPhone);
        Password = findViewById(R.id.editTextPassword1);
        Password2 = findViewById(R.id.editTextPassword2);
        Register = findViewById(R.id.SignUp);
        Login = findViewById(R.id.loginLink);


        Auth = FirebaseAuth.getInstance();
        Fstore = FirebaseFirestore.getInstance();
        ProgressBar = findViewById(R.id.progressBar);

        if(Auth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        Register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String fname = FirstName.getText().toString().trim();
                final String lname = LastName.getText().toString().trim();
                final String email = Email.getText().toString().trim();
                final String phone = Phone.getText().toString().trim();
                String password = Password.getText().toString().trim();
                String password2 = Password2.getText().toString().trim();

                if(TextUtils.isEmpty(fname)){
                    FirstName.setError("First Name is required");
                    return;
                }

                if(TextUtils.isEmpty(lname)){
                    LastName.setError("Last Name is required");
                    return;
                }

                if(TextUtils.isEmpty(email)){
                    Email.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(phone)){
                    Phone.setError("Phone is required");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Password.setError("Password is required");
                    return;
                }

                if(password.length()<6){
                    Password.setError("Password must be longer than 5 characters");
                    return;
                }

                if(!password.equals(password2)){
                    Toast.makeText(Register.this, "Password not matched", Toast.LENGTH_SHORT).show();
                    return;

                }

                

                ProgressBar.setVisibility(View.VISIBLE);

                Auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                            UserID = Auth.getCurrentUser().getUid();
                            Ref = FirebaseDatabase.getInstance().getReference("Profile Info");
                            Map<String,Object>user = new HashMap<>();
                            user.put("FirstName",fname);
                            user.put("LastName",lname);
                            user.put("Email",email);
                            user.put("Phone",phone);
                            Ref.child(UserID).setValue(user);

                            startActivity(new Intent(getApplicationContext(),Login.class));
                        }else{
                            Toast.makeText(Register.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }
}