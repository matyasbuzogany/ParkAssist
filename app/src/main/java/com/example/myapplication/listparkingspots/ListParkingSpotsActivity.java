package com.example.myapplication.listparkingspots;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.MainActivity;
import com.example.myapplication.ProfileActivity;
import com.example.myapplication.R;
import com.example.myapplication.car.CarActivity;
import com.example.myapplication.models.ParkingSpot;
import com.example.myapplication.parkingspot.MyParkingSpotsActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ListParkingSpotsActivity extends AppCompatActivity implements View.OnClickListener, IListParkingSpotsActivity, SwipeRefreshLayout.OnRefreshListener {

    private static final int REQUEST_LOCATION = 101;

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
    ListParkingSpotRecyclerViewAdapter mParkingSpotRecyclerViewAdapter;
    DocumentSnapshot lastQueriedDocument;

    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listparkingspots);

        mParentLayout = findViewById(R.id.content);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Browse Parking Spots");
        navigationView = findViewById(R.id.navigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            currentLocation = location;
            System.out.println("Current Location: " + currentLocation);
        });

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
                    drawerLayout.closeDrawers();
                    return true;
            }
            return false;
        });

        getAllParkingSpots();
        initRecyclerView();
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onRefresh() {
        getAllParkingSpots();
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void initRecyclerView() {
        if (mParkingSpotRecyclerViewAdapter == null) {
            mParkingSpotRecyclerViewAdapter = new ListParkingSpotRecyclerViewAdapter(this, mSpots);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mParkingSpotRecyclerViewAdapter);
    }


    private void sortParkingSpots( ArrayList<ParkingSpot> spots) {
        ParkingSpot temporary;
        for(int i = 0; i < spots.size(); i++) {
            for (int j = 0; j < spots.size() - i - 1; j ++) {
                if (calculateDistanceToParkingSpot(spots.get(j)) > (calculateDistanceToParkingSpot(spots.get(j+1)))) {
                    temporary = spots.get(j);
                    spots.set(j, spots.get(j+1));
                    spots.set(j+1, temporary);
                }
            }
        }
    }


    private double calculateDistanceToParkingSpot(ParkingSpot parkingSpot) {

        Location parkingSpotLocation = new Location("Parking Spot");
        parkingSpotLocation.setLatitude(Double.parseDouble(parkingSpot.getLatitude()));
        parkingSpotLocation.setLongitude(Double.parseDouble(parkingSpot.getLongitude()));

        return currentLocation.distanceTo(parkingSpotLocation);
    }




    public void getAllParkingSpots() {
        CollectionReference spotsCollectionReference = firestore.collection("parkingspots");
        Query query;

        if (lastQueriedDocument != null) {
            query = spotsCollectionReference.whereEqualTo("open", true).startAfter(lastQueriedDocument);
        } else {
            query = spotsCollectionReference.whereEqualTo("open", true);
        }

        query.get().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ParkingSpot parkingSpot = document.toObject(ParkingSpot.class);
                    mSpots.add(parkingSpot);
                }

                sortParkingSpots(mSpots);

                if (task.getResult().size() != 0) {
                    lastQueriedDocument = task.getResult().getDocuments().get(task.getResult().size()-1);
                }
                mParkingSpotRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(ListParkingSpotsActivity.this, "Query Unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });

    };




    @Override
    public void onClick(View v) {

    }




    @Override
    public void onReserveButtonClicked(ParkingSpot parkingSpot) {
        createAlertDialog(parkingSpot);
    }




    private void createAlertDialog(ParkingSpot parkingSpot) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to reserve this spot: \n" + parkingSpot.getAddress() + ", " + parkingSpot.getParkingSpotNr() + " ?");
        builder.setTitle("Confirm Reservation");

        builder.setPositiveButton("Yes", (dialog, which) -> {
            DocumentReference reference = firestore.collection("parkingspots").document(parkingSpot.getParkingSpotID());

            reference.update("open", false).addOnCompleteListener( task -> {

                if (task.isSuccessful()) {
                    String uri = "http://maps.google.com/maps?saddr=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude() + "&daddr=" + parkingSpot.getLatitude() + "," + parkingSpot.getLongitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
                else {
                    Toast.makeText(ListParkingSpotsActivity.this, "Error while reserving spot!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        builder.setNegativeButton("No", (dialog, which) -> Toast.makeText(ListParkingSpotsActivity.this, "Cancelled!", Toast.LENGTH_SHORT).show());
        builder.create();
        builder.show();
    }




















}
