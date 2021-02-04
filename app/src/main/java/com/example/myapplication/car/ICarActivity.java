package com.example.myapplication.car;

import com.example.myapplication.models.Car;

public interface ICarActivity {

    void createNewCar(String brand, String color, String productionyear, String countryOfRegistration, String numberPlate);

    void onCarSelected(Car car);

    void updateCar(Car car);

    void deleteCar(Car car);
}
