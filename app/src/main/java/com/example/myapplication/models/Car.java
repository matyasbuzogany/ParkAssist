package com.example.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Car implements Parcelable {

    private String carID;
    private String brand;
    private String color;
    private String productionYear;
    private String countryOfRegistration;
    private String numberPlate;
    private String ownerID;
    private @ServerTimestamp Date timestamp;

    public Car(){

    }


    public Car(String carID, String brand, String color, String productionYear, String countryOfRegistration, String numberplate,  String ownerID, Date timestamp) {
        this.carID = carID;
        this.brand = brand;
        this.color = color;
        this.productionYear = productionYear;
        this.countryOfRegistration = countryOfRegistration;
        this.numberPlate = numberplate;
        this.ownerID = ownerID;
        this.timestamp = timestamp;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNumberplate() {
        return numberPlate;
    }

    public void setNumberplate(String numberplate) {
        this.numberPlate = numberplate;
    }

    public String getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(String productionYear) {
        this.productionYear = productionYear;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getCountryOfRegistration() {
        return countryOfRegistration;
    }

    public void setCountryOrRegistration(String countryOrRegistration) {
        this.countryOfRegistration = countryOrRegistration;
    }

    protected Car(Parcel in) {
        carID = in.readString();
        brand = in.readString();
        color = in.readString();
        productionYear = in.readString();
        countryOfRegistration = in.readString();
        numberPlate = in.readString();
        ownerID = in.readString();
    }

    public static final Creator<Car> CREATOR = new Creator<Car>() {
        @Override
        public Car createFromParcel(Parcel in) {
            return new Car(in);
        }

        @Override
        public Car[] newArray(int size) {
            return new Car[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(carID);
        dest.writeString(brand);
        dest.writeString(color);
        dest.writeString(productionYear);
        dest.writeString(countryOfRegistration);
        dest.writeString(numberPlate);
        dest.writeString(ownerID);
    }
}
