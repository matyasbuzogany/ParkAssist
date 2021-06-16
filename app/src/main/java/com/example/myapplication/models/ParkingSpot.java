package com.example.myapplication.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;


import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ParkingSpot implements Parcelable{

    private String parkingSpotID;
    private String parkingSpotNr;
    private String userID;
    private String address;
    private String city;
    private String country;
    private String postcode;
    private String latitude;
    private String longitude;
    private boolean open;
    private @ServerTimestamp Date timestamp;

    public ParkingSpot() {

    }

    public ParkingSpot(String parkingSpotID, String parkingSpotNr, String userID, String address, String city, String country, String postcode, String latitude, String longitude, boolean isOpen, Date timestamp) {
        this.parkingSpotID = parkingSpotID;
        this.parkingSpotNr = parkingSpotNr;
        this.userID = userID;
        this.address = address;
        this.city = city;
        this.country = country;
        this.postcode = postcode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.open = isOpen;
        this.timestamp = timestamp;
    }


    public String getParkingSpotID() {
        return parkingSpotID;
    }

    public void setParkingSpotID(String parkingSpotID) {
        this.parkingSpotID = parkingSpotID;
    }

    public String getParkingSpotNr() {
        return parkingSpotNr;
    }

    public void setParkingSpotNr(String parkingSpotNr) {
        this.parkingSpotNr = parkingSpotNr;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public boolean getOpen() {
        return open;
    }

    public void setOpen(boolean isOpen) {
        open = isOpen;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected ParkingSpot(Parcel in) {
        parkingSpotID = in.readString();
        parkingSpotNr = in.readString();
        userID = in.readString();
        address = in.readString();
        city = in.readString();
        country = in.readString();
        postcode = in.readString();
        open = in.readBoolean();
        latitude = in.readString();
        longitude = in.readString();
    }

    public static final Creator<ParkingSpot> CREATOR = new Creator<ParkingSpot>() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public ParkingSpot createFromParcel(Parcel source) {
            return new ParkingSpot(source);
        };

        @Override
        public ParkingSpot[] newArray(int size) {
            return new ParkingSpot[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(parkingSpotID);
        dest.writeString(parkingSpotNr);
        dest.writeString(userID);
        dest.writeString(address);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(postcode);
        dest.writeBoolean(open);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

}
