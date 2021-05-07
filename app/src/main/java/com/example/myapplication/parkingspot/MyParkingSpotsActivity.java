package com.example.myapplication.parkingspot;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.car.CarActivity;
import com.example.myapplication.models.ParkingSpot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MyParkingSpotsActivity extends AppCompatActivity implements View.OnClickListener, IMyParkingSpotsActivity, SwipeRefreshLayout.OnRefreshListener {

    FloatingActionButton floatingActionButton;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    View mParentLayout;
    ArrayList<ParkingSpot> mSpots = new ArrayList<>();
    ParkingSpotRecyclerViewAdapter mParkingSpotRecyclerViewAdapter;
    DocumentSnapshot lastQueriedDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_parking_spots);

        floatingActionButton = findViewById(R.id.floatingButton);
        floatingActionButton.setOnClickListener(this);
        mParentLayout = findViewById(R.id.content);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Parking Spots");
        navigationView = findViewById(R.id.navigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {

            final int id = item.getItemId();

            switch(id) {
                case R.id.menuMyProfile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuMyCars:
                    startActivity(new Intent(getApplicationContext(), CarActivity.class));
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuMyParkingSpots:
                    Toast.makeText(MyParkingSpotsActivity.this, "My Parking Spots Selected!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuHome:
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuSettings:
                    Toast.makeText(MyParkingSpotsActivity.this, "Settings Selected!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuLogout:
                    Toast.makeText(MyParkingSpotsActivity.this, "Logout Selected!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    return true;
            }
            return false;
        });

        floatingActionButton.setOnClickListener( v -> {
            NewParkingSpotDialog dialog = new NewParkingSpotDialog();
            dialog.show(getSupportFragmentManager(), "New Parking Spot Dialog");
        });

        initRecyclerView();
        getSpotsForCurrentUser();
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }



    @Override
    public void createNewSpot(String parkingSpotNr, String address, String city, String country, String postcode, boolean open) {
        DocumentReference newSpotReference = firestore.collection("parkingspots").document();

        ParkingSpot parkingSpot = new ParkingSpot();
        parkingSpot.setParkingSpotNr(parkingSpotNr);
        parkingSpot.setParkingSpotID(newSpotReference.getId());
        parkingSpot.setAddress(address);
        parkingSpot.setCity(city);
        parkingSpot.setCountry(country);
        parkingSpot.setPostcode(postcode);
        parkingSpot.setOpen(open);

        parkingSpot.setUserID(userID);

        newSpotReference.set(parkingSpot).addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MyParkingSpotsActivity.this, "Parking Spot added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyParkingSpotsActivity.this, "Error Adding Parking Spot to database!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onSpotSelected(ParkingSpot parkingSpot) {
        ViewParkingSpotDialog dialog = ViewParkingSpotDialog.newInstance(parkingSpot);
        dialog.show(getSupportFragmentManager(), "Update your Parking Spot");
    }



    @Override
    public void updateSpot(ParkingSpot parkingSpot) {
        DocumentReference reference = firestore.collection("parkingspots").document(parkingSpot.getParkingSpotID());
        reference.update("address", parkingSpot.getAddress(),
                "country", parkingSpot.getCountry(),
                "city", parkingSpot.getCity(),
                "postcode", parkingSpot.getPostcode(),
                "parkingSpotNr", parkingSpot.getParkingSpotNr(),
                "open", parkingSpot.getOpen()).addOnCompleteListener( task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MyParkingSpotsActivity.this, "Parking Spot updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MyParkingSpotsActivity.this, "Error while updating!", Toast.LENGTH_SHORT).show();

                    }
        });
    }



    @Override
    public void deleteSpot(ParkingSpot parkingSpot) {
        DocumentReference reference = firestore.collection("parkingspots").document(parkingSpot.getParkingSpotID());
        reference.delete().addOnCompleteListener( task ->  {
            if (task.isSuccessful()) {
                Toast.makeText(MyParkingSpotsActivity.this, "Parking Spot deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyParkingSpotsActivity.this, "Error while deleting!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        getSpotsForCurrentUser();
        mSwipeRefreshLayout.setRefreshing(false);
    }




    public void getSpotsForCurrentUser() {
        CollectionReference spotsCollectionReference = firestore.collection("parkingspots");

        Query spotsQuery;

        if (lastQueriedDocument != null) {
            System.out.println("USERID: " + userID);
            spotsQuery = spotsCollectionReference.whereEqualTo("userID", userID).orderBy("timestamp", Query.Direction.ASCENDING).startAfter(lastQueriedDocument);
        } else {
            System.out.println("USERID: " + userID);
            spotsQuery = spotsCollectionReference.whereEqualTo("userID", userID).orderBy("timestamp", Query.Direction.ASCENDING);
        }


        spotsQuery.get().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                for(QueryDocumentSnapshot document : task.getResult()) {
                    ParkingSpot parkingSpot = document.toObject(ParkingSpot.class);
                    mSpots.add(parkingSpot);
                }

                if (task.getResult().size() != 0) {
                    lastQueriedDocument = task.getResult().getDocuments().get(task.getResult().size()-1);
                }
                mParkingSpotRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MyParkingSpotsActivity.this, "Query unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initRecyclerView() {
        if (mParkingSpotRecyclerViewAdapter == null) {
            mParkingSpotRecyclerViewAdapter = new ParkingSpotRecyclerViewAdapter(this, mSpots);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mParkingSpotRecyclerViewAdapter);
    }
}