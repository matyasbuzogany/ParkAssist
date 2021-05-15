package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.myapplication.models.ParkingSpot;
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

    private static final int REQUEST_LOCATION = 1;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    String userID;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

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


        // Add a marker in Cluj and move the camera
        LatLng current = new LatLng(46.7712, 23.6236);
//        mMap.addMarker(new MarkerOptions().position(current).title("Marker in Cluj-Napoca").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(current));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(46.7712, 23.6236), 12.0f));


    }


    public void addParkingSpotMarker(String latitude, String longitude, String address, String number) {

        LatLng latlng = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
        mMap.addMarker(new MarkerOptions().position(latlng).title(address + ", Parking Spot Number: " + number).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
    }



    public void getCurrentLocation() {

    }


}