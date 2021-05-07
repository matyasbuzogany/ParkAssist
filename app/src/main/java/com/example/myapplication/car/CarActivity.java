package com.example.myapplication.car;

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
import com.example.myapplication.models.Car;
import com.example.myapplication.parkingspot.MyParkingSpotsActivity;
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

public class  CarActivity extends AppCompatActivity implements View.OnClickListener, ICarActivity, SwipeRefreshLayout.OnRefreshListener {

    //widgets
    FloatingActionButton floatingActionButton;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;

    //general
    Toolbar toolbar;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    String userID;

    //Variables
    View mParentLayout;
    ArrayList<Car> mCars = new ArrayList<>();
    CarRecyclerViewAdapter mCarRecyclerViewAdapter;
    DocumentSnapshot lastQueriedDocument; //for refresh, in order to not load it multiple times

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        floatingActionButton = findViewById(R.id.floatingButton);
        mParentLayout = findViewById(android.R.id.content);
        floatingActionButton.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        //Setting up Toolbar
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Cars");
        navigationView = findViewById(R.id.navigationView);

        //Setting up Drawer
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        //Setting up Navigation View
        navigationView.setNavigationItemSelectedListener(item -> {

            final int id = item.getItemId();

            switch(id) {
                case R.id.menuMyProfile:
                    startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuMyCars:
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
                    Toast.makeText(CarActivity.this, "Settings Selected!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuLogout:
                    Toast.makeText(CarActivity.this, "Logout Selected!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    return true;
            }
            return false;
        });

        floatingActionButton.setOnClickListener(v -> {
            NewCarDialog dialog = new NewCarDialog();
            dialog.show(getSupportFragmentManager(), "New Car Dialog");
        });

        initRecyclerView();
        getCarsForCurrentUser();
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }


    @Override
    public void onClick(View v) {
//        switch (navigationView.getId()) {
//            case R.id.floatingButton:
//                NewCarDialog dialog = new NewCarDialog();
//                dialog.show(getSupportFragmentManager(), "New Car Dialog");
//                break;
//        }
    }



    //ICarActivity interface
    @Override
    public void createNewCar(String brand, String color, String productionYear, String countryOfRegistration, String numberPlate ) {
        DocumentReference newCarRef = firestore.collection("cars").document();
        Car car = new Car();
        car.setCarID(newCarRef.getId());
        car.setBrand(brand);
        car.setColor(color);
        car.setProductionYear(productionYear);
        car.setCountryOrRegistration(countryOfRegistration);
        car.setNumberplate(numberPlate);

        car.setOwnerID(userID);

        newCarRef.set(car).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CarActivity.this, "Car added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CarActivity.this, "Error Adding car to database!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Firestore automatically creates indexes, so it is efficient to query it
    public void getCarsForCurrentUser() {
        CollectionReference carsCollectionRef = firestore.collection("cars");

        Query carsQuery;
        if (lastQueriedDocument != null) {
            carsQuery = carsCollectionRef.whereEqualTo("ownerID", userID).orderBy("timestamp", Query.Direction.ASCENDING).startAfter(lastQueriedDocument);

        } else {
            carsQuery = carsCollectionRef.whereEqualTo("ownerID", userID).orderBy("timestamp", Query.Direction.ASCENDING);
        }

        //in order to refresh to work we have to check whether the data is loaded with the last query, only change the recyclerview when it finds new data
        carsQuery.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document: task.getResult()) {
                    Car car = document.toObject(Car.class);
                    mCars.add(car);
                }
                if (task.getResult().size() != 0 ) {
                    lastQueriedDocument = task.getResult().getDocuments().get(task.getResult().size()-1);
                }
                mCarRecyclerViewAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(CarActivity.this, "Query unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onCarSelected(Car car) {
        ViewCarDialog dialog = ViewCarDialog.newInstance(car);
        dialog.show(getSupportFragmentManager(), "Update your car information");
    }



    @Override
    public void updateCar(Car car) {
        DocumentReference reference = firestore.collection("cars").document(car.getCarID());
        reference.update("brand", car.getBrand(),
                "color", car.getColor(),
                "productionYear", car.getProductionYear(),
                "countryOfRegistration", car.getCountryOfRegistration(),
                "numberplate", car.getNumberplate()).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CarActivity.this, "Car updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CarActivity.this, "Error while updating!", Toast.LENGTH_SHORT).show();
            }});
    }



    @Override
    public void deleteCar(Car car) {
        DocumentReference reference = firestore.collection("cars").document(car.getCarID());
        reference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(CarActivity.this, "Car deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CarActivity.this, "Error deleting Car!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Swipe Refresh Layout
    @Override
    public void onRefresh() {
        getCarsForCurrentUser();
        mSwipeRefreshLayout.setRefreshing(false);
    }


    private void initRecyclerView() {
        if(mCarRecyclerViewAdapter == null){
            mCarRecyclerViewAdapter = new CarRecyclerViewAdapter(this, mCars);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mCarRecyclerViewAdapter);
    }



}