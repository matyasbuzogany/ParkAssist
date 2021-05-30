package com.example.myapplication.listparkingspots;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.example.myapplication.parkingspot.ParkingSpotRecyclerViewAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ListParkingSpotsActivity extends AppCompatActivity implements View.OnClickListener, IListParkingSpotsActivity, SwipeRefreshLayout.OnRefreshListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        initRecyclerView();
        getAllParkingSpots();
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




    public void getAllParkingSpots() {
        CollectionReference spotsCollectionReference = firestore.collection("parkingspots");
        Query query;

        if (lastQueriedDocument != null) {
            query = spotsCollectionReference.orderBy("timestamp", Query.Direction.ASCENDING).startAfter(lastQueriedDocument);
        } else {
            query = spotsCollectionReference.orderBy("timestamp", Query.Direction.ASCENDING);
        }

        query.get().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ParkingSpot parkingSpot = document.toObject(ParkingSpot.class);
                    mSpots.add(parkingSpot);
                }

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
        //TODO Reserve in backend
        LatLng current = new LatLng(46.773018, 23.595214);
        String uri = "http://maps.google.com/maps?saddr=" + current.latitude + "," + current.longitude + "&daddr=" + parkingSpot.getLatitude() + "," + parkingSpot.getLongitude();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }
}
