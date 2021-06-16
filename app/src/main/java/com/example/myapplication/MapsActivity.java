package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.models.ParkingSpot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 101;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();
        firestore = FirebaseFirestore.getInstance();
        CollectionReference parkingSpotsReference = firestore.collection("parkingspots");
        Query spotsQuery = parkingSpotsReference.whereEqualTo("open", true); //.whereNotEqualTo("userID", userID);

        spotsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ParkingSpot parkingSpot = document.toObject(ParkingSpot.class);
                    addParkingSpotMarker(parkingSpot.getLatitude(), parkingSpot.getLongitude(), parkingSpot.getAddress(), parkingSpot.getParkingSpotNr());
                }
            } else {
                Toast.makeText(this, "Query unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });

        // Add a marker to current position
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, location -> {
            System.out.println("CURRENT LOC: " + location);

            LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15.0f));
        });

    }



    public void addParkingSpotMarker(String latitude, String longitude, String address, String number) {

        LatLng latlng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        mMap.addMarker(new MarkerOptions().position(latlng).title(address + ", Parking Spot Number: " + number).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
    }



}