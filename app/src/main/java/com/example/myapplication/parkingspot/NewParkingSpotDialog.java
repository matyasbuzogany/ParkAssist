package com.example.myapplication.parkingspot;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;

public class NewParkingSpotDialog extends DialogFragment implements View.OnClickListener {

    private EditText mAddress, mCountry, mCity, mPostcode, mNumber;
    private Switch mSwitch;
    private Button mCreate, mCancel;

    IMyParkingSpotsActivity iMyParkingSpotsActivity;



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
        View view = inflater.inflate(R.layout.dialog_new_parking_spot, container, false);
        mAddress = view.findViewById(R.id.spot_address);
        mCountry = view.findViewById(R.id.spot_country);
        mCity = view.findViewById(R.id.spot_city);
        mPostcode = view.findViewById(R.id.spot_postcode);
        mNumber = view.findViewById(R.id.spot_number);

        mSwitch = view.findViewById(R.id.switch1);

        mCreate = view.findViewById(R.id.spot_create);
        mCancel = view.findViewById(R.id.spot_cancel);

        mCancel.setOnClickListener(this);
        mCreate.setOnClickListener(this);

        getDialog().setTitle("Add a New Parking Spot");

        return view;
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.spot_create:
                String address = mAddress.getText().toString().trim();
                String country = mCountry.getText().toString().trim();
                String city = mCity.getText().toString().trim();
                String postcode = mPostcode.getText().toString().trim();
                String number = mNumber.getText().toString().trim();
                boolean open = mSwitch.isChecked();
                System.out.println("IS CHECKED: " + open);

                if (!address.equals("")) {
                    iMyParkingSpotsActivity.createNewSpot(number, address, city, country, postcode, open);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "You have to enter an Address!", Toast.LENGTH_SHORT).show();
                }
            case R.id.spot_cancel:
                getDialog().dismiss();
                break;
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iMyParkingSpotsActivity = (IMyParkingSpotsActivity) getActivity();
    }
}
