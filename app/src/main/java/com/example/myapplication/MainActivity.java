package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.car.CarActivity;
import com.example.myapplication.listparkingspots.ListParkingSpotsActivity;
import com.example.myapplication.models.Car;
import com.example.myapplication.models.User;
import com.example.myapplication.parkingspot.MyParkingSpotsActivity;
import com.example.myapplication.user.ViewUserDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IMainActivity {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { //implemented later
        return false;
    }


    public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> { //adapter for displaying ceratain options is recycler view as card view

        List<String> titles;
        List<Integer> images;
        LayoutInflater inflater;

        public GridAdapter(Context context, List<String> titles, List<Integer> images) {
            this.titles = titles;
            this.images = images;
            this.inflater = LayoutInflater.from(context);
        }


        @NonNull
        @Override
        public GridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);
            return new GridAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GridAdapter.ViewHolder holder, int position) {
            holder.textView.setText(titles.get(position));
            holder.imageView.setImageResource(images.get(position));
        }

        @Override
        public int getItemCount() {
            return titles.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView textView;
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textViewCustom);
                imageView = itemView.findViewById(R.id.imageViewCustom);

                itemView.setOnClickListener(v -> {

                    //Scan a Numberplate
                    if (getAdapterPosition() == 0) {
                        scanANumberPlate();
                    }

                    //Enter a Numberplate
                    if (getAdapterPosition() == 1) {
                        enterANumberplate();
                    }

                    //Open maps with the parking spots available
                    if (getAdapterPosition() == 2) {
                        if (isServicesOK()) {
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(intent);
                        }
                        Toast.makeText(v.getContext(), "Opening the Map", Toast.LENGTH_SHORT).show();
                    }

                    //List all parking spots
                    if (getAdapterPosition() == 3) {
                        Toast.makeText(v.getContext(), "Listing all available Parking Spots!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), ListParkingSpotsActivity.class));
                    }

                    //Logout
                    if (getAdapterPosition() == 4) {
                        Toast.makeText(v.getContext(), "Logging out! ", Toast.LENGTH_SHORT).show();
                        logout();
                    }
                });
            }
        }
    }




    FirebaseFirestore firestore;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    RecyclerView mDataList;
    List<String> titles;
    List<Integer> images;
    GridAdapter adapter;



    public boolean isServicesOK() {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything fine
            System.out.println("Google play services working!");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //an error occured
            System.out.println("An error occured bu we can fix it!");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, 9001);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests!", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //requestCode = any number
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 103);
        }

        if (checkSelfPermission(Manifest.permission.LOCATION_HARDWARE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.LOCATION_HARDWARE}, 104);
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 102);
        }



        firestore = FirebaseFirestore.getInstance();

        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.navigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        mDataList = findViewById(R.id.dataList);
        titles = new ArrayList<>();
        images = new ArrayList<>();

        titles.add("Scan a Number Plate");
        titles.add("Enter a Numberplate");
        titles.add("Open Maps");
        titles.add("List all Parking Spots");
        titles.add("Logout");

        images.add(R.drawable.ic_camera_white);
        images.add(R.drawable.ic_numberplate_white);
        images.add(R.drawable.ic_map_white);
        images.add(R.drawable.ic_park_white);
        images.add(R.drawable.ic_exit_white);

        adapter = new GridAdapter(this, titles, images);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        mDataList.setLayoutManager(gridLayoutManager);
        mDataList.setAdapter(adapter);


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
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuSettings:
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuLogout:
                    Toast.makeText(MainActivity.this, "Logging out!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    logout();
                    return true;
            }
            return false;
        });

    }



    public void scanANumberPlate() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 101);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");


        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVision firebaseVision = FirebaseVision.getInstance();
        FirebaseVisionTextRecognizer recognizer = firebaseVision.getOnDeviceTextRecognizer();

        Task<FirebaseVisionText> task = recognizer.processImage(image);

        task.addOnSuccessListener(firebaseVisionText -> {


            String text = firebaseVisionText.getText(); // text should be the license plate
            String trimmedText = text.replaceAll("\\s+", "");
            System.out.println("TEXT FOUND IN IMAGE: " + trimmedText);

            CollectionReference carsCollectionReference = firestore.collection("cars");
            Query carsQuery =  carsCollectionReference.whereEqualTo("numberplate", trimmedText);

            carsQuery.get().addOnCompleteListener( task1 -> {
                if(task1.isSuccessful()) {

                    for (QueryDocumentSnapshot document: task1.getResult()) {

                        Car car = document.toObject(Car.class);
                        DocumentReference doc = firestore.collection("users").document(car.getOwnerID());

                        doc.addSnapshotListener(this, (value, error) -> {

                            User user = new User(value.getString("email"), value.getString("firstName"), value.getString("lastName"), value.getString("phoneNumber"));
                            ViewUserDialog dialog = ViewUserDialog.newInstance(user);
                            dialog.show(getSupportFragmentManager(),"Owner of the Vehicle");
                        });
                    }

                } if (task1.getResult().size() == 0) {
                    System.out.println("Not Found");
                    Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();

                } else if (!task1.isSuccessful()) {
                    Toast.makeText(this, "Query unsuccessful!", Toast.LENGTH_SHORT).show();
                }
            });
        });

        task.addOnFailureListener(System.out::println);
    }



    public void enterANumberplate() {
        EnterNumberplateDialog dialog = new EnterNumberplateDialog();
        dialog.show(getSupportFragmentManager(), "Search for a Numberplate");
    }


    @Override
    public void getCarForNumberplate(String numberplate) {
        CollectionReference carsCollectionReference = firestore.collection("cars");
        String uppercases = numberplate.toUpperCase();
        Query carsQuery =  carsCollectionReference.whereEqualTo("numberplate", uppercases);
        System.out.println("NUMBERPLATE: " + uppercases);

        carsQuery.get().addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                    Car car = documentSnapshot.toObject(Car.class);
                    DocumentReference documentReference = firestore.collection("users").document(car.getOwnerID());

                    documentReference.addSnapshotListener(this, (value, error) -> {
                        User user = new User(value.getString("email"), value.getString("firstName"), value.getString("lastName"), value.getString("phoneNumber"));
                        ViewUserDialog dialog = ViewUserDialog.newInstance(user);
                        dialog.show(getSupportFragmentManager(), "Owner of the Vehicle;");
                    });
                }
            } if (task.getResult().size() == 0) {
                Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            } else if (!task.isSuccessful()) {
                Toast.makeText(this, "Query unsuccessful!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }


}