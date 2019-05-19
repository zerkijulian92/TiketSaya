package com.takatutustudio.tiketapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterOneAct extends AppCompatActivity {

    LinearLayout btn_back;
    EditText username, password, email_address;
    DatabaseReference reference, reference_username;
    Button btn_continue;

    String USERNAME_KEY = "usernamekey";
    String username_key = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email_address = findViewById(R.id.email_address);

        btn_back = findViewById(R.id.btn_back);

        btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ubah state menjadi loading
                btn_continue.setEnabled(false);
                btn_continue.setText("Loading...");

                //mengambil username pada firebase
                reference_username = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(username.getText().toString());

                reference_username.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Jika username tersedia
                        if (dataSnapshot.exists()){
                            Toast.makeText(getApplicationContext(), "Username sudah tersedia", Toast.LENGTH_SHORT).show();

                            //ubah state menjadi loading Active
                            btn_continue.setEnabled(true);
                            btn_continue.setText("CONTINUE");

                        }
                        else {
                            //menyimpan data kepada local storage (handphone)
                            SharedPreferences sharedPreferences = getSharedPreferences(USERNAME_KEY, MODE_PRIVATE);
                            SharedPreferences.Editor editor =  sharedPreferences.edit();
                            editor.putString(username_key, username.getText().toString());
                            editor.apply();

                            //simpan kepada database
                            reference = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(username.getText().toString());
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    dataSnapshot.getRef().child("username").setValue(username.getText().toString());
                                    dataSnapshot.getRef().child("password").setValue(password.getText().toString());
                                    dataSnapshot.getRef().child("email_address").setValue(email_address.getText().toString());
                                    dataSnapshot.getRef().child("user_balance").setValue(800);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            //berpindah activity
                            Intent gotonextregister = new Intent(RegisterOneAct.this, RegisterTwoAct.class);
                            startActivity(gotonextregister);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }
}
