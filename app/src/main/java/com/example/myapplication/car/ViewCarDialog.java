package com.example.myapplication.car;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.models.Car;

public class ViewCarDialog extends DialogFragment implements View.OnClickListener {


    private EditText mBrand, mColor,mYear, mCountry, mNumberplate;
    private Button mUpdate, mDelete;

    ICarActivity iCarActivity;

    Car mCar;

    public static ViewCarDialog newInstance(Car car) {
        ViewCarDialog dialog = new ViewCarDialog();

        Bundle args = new Bundle();
        args.putParcelable("car", car);
        dialog.setArguments(args);

        return dialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Dialog_MinWidth;
        setStyle(style, theme);

        mCar = getArguments().getParcelable("car");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_car, container, false);
        mBrand = view.findViewById(R.id.car_brand);
        mColor = view.findViewById(R.id.car_color);
        mYear = view.findViewById(R.id.car_year);
        mCountry = view.findViewById(R.id.car_country);
        mNumberplate = view.findViewById(R.id.car_numberplate);
        mUpdate = view.findViewById(R.id.update);
        mDelete = view.findViewById(R.id.delete);

        mDelete.setOnClickListener(this);
        mUpdate.setOnClickListener(this);

        getDialog().setTitle("Update Your Car");

        setInitialProperties();

        return view;
    }


    private void setInitialProperties() {
        mBrand.setText(mCar.getBrand());
        mColor.setText(mCar.getColor());
        mYear.setText(mCar.getProductionYear());
        mCountry.setText(mCar.getCountryOfRegistration());
        mNumberplate.setText(mCar.getNumberplate());
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.update):
                String brand = mBrand.getText().toString().trim();
                String color = mColor.getText().toString().trim();
                String year = mYear.getText().toString().trim();
                String country = mCountry.getText().toString().trim();
                String numberplate = mNumberplate.getText().toString().trim();

                if (!numberplate.equals("") && !brand.equals("")) {
                    mCar.setBrand(brand);
                    mCar.setColor(color);
                    mCar.setProductionYear(year);
                    mCar.setCountryOrRegistration(country);
                    mCar.setNumberplate(numberplate);

                    iCarActivity.updateCar(mCar);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "You have to enter a brand and numberplate!", Toast.LENGTH_SHORT).show();
                }
                break;
            case(R.id.delete):
                iCarActivity.deleteCar(mCar);
                getDialog().dismiss();
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iCarActivity = (ICarActivity) getActivity();
    }
}
