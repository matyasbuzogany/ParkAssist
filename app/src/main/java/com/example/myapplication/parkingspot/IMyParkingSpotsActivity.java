package com.example.myapplication.parkingspot;

import com.example.myapplication.models.ParkingSpot;

public interface IMyParkingSpotsActivity {

    void createNewSpot(String parkingSpotNr, String address, String city, String country, String postcode, boolean isOpen);

    void onSpotSelected(ParkingSpot parkingSpot);

    void updateSpot(ParkingSpot parkingSpot);

    void deleteSpot(ParkingSpot parkingSpot);
}
