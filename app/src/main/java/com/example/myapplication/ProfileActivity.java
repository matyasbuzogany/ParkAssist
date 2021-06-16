package com.example.myapplication;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myapplication.car.CarActivity;
import com.example.myapplication.parkingspot.MyParkingSpotsActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    EditText mFirstName, mLastName, mPhone, mEmail;

    Button mCancelButton, mSaveButton;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirstName = findViewById(R.id.editTextFirstName);
        mLastName = findViewById(R.id.editTextLastName);
        mPhone = findViewById(R.id.editTextPhone);
        mEmail = findViewById(R.id.editTextEmail);

        mCancelButton = findViewById(R.id.buttonCancel);
        mSaveButton = findViewById(R.id.buttonSave);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        //Setting up Toolbar
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        navigationView = findViewById(R.id.navigationView);

        //Setting up Drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        userID = firebaseAuth.getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, (value, error) -> {
                mFirstName.setText(value.getString("firstName"));
                mLastName.setText(value.getString("lastName"));
                mEmail.setText(value.getString("email"));
                mPhone.setText(value.getString("phoneNumber"));
        });

        //Setting up Navigation View
        navigationView.setNavigationItemSelectedListener(item -> {

            final int id = item.getItemId();

            switch(id) {
                case R.id.menuMyProfile:
                    Toast.makeText(ProfileActivity.this, "You are at My Profil Page!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuMyCars:
                    startActivity(new Intent(getApplicationContext(), CarActivity.class));
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuMyParkingSpots:
                    startActivity(new Intent(getApplicationContext(), MyParkingSpotsActivity.class));
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuHome:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuSettings:
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuLogout:
                    Toast.makeText(ProfileActivity.this, "Logging out!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    logout();
                    return true;
            }
            return false;
        });


        mSaveButton.setOnClickListener(v -> {
            String firstName = mFirstName.getText().toString().trim();
            String lastName = mLastName.getText().toString().trim();
            String phoneNumber = mPhone.getText().toString().trim();

            documentReference.update("firstName", firstName,
                    "lastName", lastName,
                    "phoneNumber", phoneNumber).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "User updated Succesfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Error Updating First Name!", Toast.LENGTH_SHORT).show();
                }
            });
        });


        mCancelButton.setOnClickListener( v-> {
            documentReference.addSnapshotListener(this, (value, error) -> {
                mFirstName.setText(value.getString("firstName"));
                mLastName.setText(value.getString("lastName"));
                mPhone.setText(value.getString("phoneNumber"));

                Toast.makeText(ProfileActivity.this, "Changes reverted!", Toast.LENGTH_SHORT).show();

            });
        });

    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

}