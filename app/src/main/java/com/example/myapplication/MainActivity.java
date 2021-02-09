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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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


                    if (getAdapterPosition() == 0) {
                        //TODO
                        Toast.makeText(v.getContext(), "Clicked -> " + titles.get(getAdapterPosition()) + ", not implemented yet", Toast.LENGTH_SHORT).show();
                        scanANumberPlate();
                    }

                    if (getAdapterPosition() == 1) {
                        //TODO
                        Toast.makeText(v.getContext(), "Clicked -> " + titles.get(getAdapterPosition()) + ", not implemented yet", Toast.LENGTH_SHORT).show();
                    }

                    if (getAdapterPosition() == 2) {
                        //TODO
                        Toast.makeText(v.getContext(), "Clicked -> " + titles.get(getAdapterPosition()) + ", not implemented yet", Toast.LENGTH_SHORT).show();
                    }

                    if (getAdapterPosition() == 3) {
                        Toast.makeText(v.getContext(), "Logged out! ", Toast.LENGTH_SHORT).show();
                        logout();
                    }
                });
            }
        }
    }




    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    Toolbar toolbar;
    NavigationView navigationView;
    RecyclerView mDataList;
    List<String> titles;
    List<Integer> images;
    GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //requedtcode = any number
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        }

        if (checkSelfPermission(Manifest.permission.LOCATION_HARDWARE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.LOCATION_HARDWARE}, 102);
        }

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
        titles.add("Look for Parking Spots");
        titles.add("Generate QR Code");
        titles.add("Logout");

        images.add(R.drawable.ic_camera_white);
        images.add(R.drawable.ic_park_white);
        images.add(R.drawable.ic_qrcode_white);
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
                    Toast.makeText(MainActivity.this, "My Parking Spots Selected!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuHome:
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuSettings:
                    Toast.makeText(MainActivity.this, "Settings Selected!", Toast.LENGTH_SHORT).show();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.menuLogout:
                    Toast.makeText(MainActivity.this, "Logout Selected!", Toast.LENGTH_SHORT).show();
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
            String text = firebaseVisionText.getText();
            System.out.println("TEXT FOUND IN IMAGE: " + text);
            Toast.makeText(MainActivity.this, "Successful -> " + text, Toast.LENGTH_SHORT).show();
        });
        task.addOnFailureListener(System.out::println);
    }




    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }


}