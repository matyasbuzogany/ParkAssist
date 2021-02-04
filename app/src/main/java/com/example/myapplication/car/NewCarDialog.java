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
import com.example.myapplication.car.ICarActivity;

public class NewCarDialog extends DialogFragment implements View.OnClickListener {

    private EditText mBrand, mColor,mYear, mCountry, mNumberplate;
    private Button mCreate, mCancel;

    ICarActivity iCarActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Dialog_MinWidth;
        setStyle(style, theme);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_new_car, container, false);
        mBrand = view.findViewById(R.id.car_brand);
        mColor = view.findViewById(R.id.car_color);
        mYear = view.findViewById(R.id.car_year);
        mCountry = view.findViewById(R.id.car_country);
        mNumberplate = view.findViewById(R.id.car_numberplate);
        mCreate = view.findViewById(R.id.create);
        mCancel = view.findViewById(R.id.cancel);

        mCancel.setOnClickListener(this);
        mCreate.setOnClickListener(this);

        getDialog().setTitle("Add New Car");

        return view;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create:
                String brand = mBrand.getText().toString().trim();
                String color = mColor.getText().toString().trim();
                String year = mYear.getText().toString().trim();
                String country = mCountry.getText().toString().trim();
                String numberplate = mNumberplate.getText().toString().trim();

                if(!numberplate.equals("") && !brand.equals("")){
                    iCarActivity.createNewCar(brand, color, year, country, numberplate);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "You have to enter a brand and numberplate!", Toast.LENGTH_SHORT).show();
                }
            case R.id.cancel:
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
