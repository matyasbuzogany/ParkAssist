package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.car.CarActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) { //implemented later
        return false;
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> { //adapter for displaying ceratain options is recycler view as card view

        List<String> titles;
        List<Integer> images;
        LayoutInflater inflater;

        public Adapter(Context context, List<String> titles, List<Integer> images) {
            this.titles = titles;
            this.images = images;
            this.inflater = LayoutInflater.from(context);
        }


        @NonNull
        @Override
        public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);
            return new Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
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
    Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        adapter = new Adapter(this, titles, images);

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


    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }


}