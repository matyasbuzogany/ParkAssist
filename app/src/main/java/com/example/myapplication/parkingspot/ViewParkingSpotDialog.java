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
import com.example.myapplication.models.ParkingSpot;

public class ViewParkingSpotDialog extends DialogFragment implements View.OnClickListener {

    private EditText mAddress, mCountry, mCity, mPostcode, mNumber;
    private Switch mSwitch;
    private Button mUpdate, mDelete;

    IMyParkingSpotsActivity iMyParkingSpotsActivity;

    ParkingSpot mParkingSpot;


    public static ViewParkingSpotDialog newInstance(ParkingSpot parkingSpot) {
        ViewParkingSpotDialog dialog = new ViewParkingSpotDialog();

        Bundle args = new Bundle();

        args.putParcelable("parkingspot", parkingSpot);
        dialog.setArguments(args);

        return dialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Dialog_MinWidth;
        setStyle(style, theme);

        mParkingSpot = getArguments().getParcelable("parkingspot");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_parking_spot, container, false);
        mAddress = view.findViewById(R.id.view_spot_address);
        mCountry = view.findViewById(R.id.view_spot_country);
        mCity = view.findViewById(R.id.view_spot_city);
        mPostcode = view.findViewById(R.id.view_spot_postcode);
        mNumber = view.findViewById(R.id.view_spot_number);

        mSwitch = view.findViewById(R.id.view_switch1);

        mDelete = view.findViewById(R.id.view_spot_delete);
        mUpdate = view.findViewById(R.id.view_spot_update);

        mDelete.setOnClickListener(this);
        mUpdate.setOnClickListener(this);

        getDialog().setTitle("Update Your Parking Spot");

        setInitialProperties();

        return view;
    }



    private void setInitialProperties() {
        mAddress.setText(mParkingSpot.getAddress());
        mCountry.setText(mParkingSpot.getCountry());
        mCity.setText(mParkingSpot.getCity());
        mPostcode.setText(mParkingSpot.getPostcode());
        mNumber.setText(mParkingSpot.getParkingSpotNr());
        if (mParkingSpot.getOpen()) {
            mSwitch.setChecked(true);
        } else {
            mSwitch.setChecked(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_spot_update:
                String address = mAddress.getText().toString().trim();
                String country = mCountry.getText().toString().trim();
                String city = mCity.getText().toString().trim();
                String postcode = mPostcode.getText().toString().trim();
                String number = mNumber.getText().toString().trim();
                boolean isClicked = mSwitch.isChecked();


                if (!address.equals("")) {
                    mParkingSpot.setAddress(address);
                    mParkingSpot.setCountry(country);
                    mParkingSpot.setCity(city);
                    mParkingSpot.setPostcode(postcode);
                    mParkingSpot.setParkingSpotNr(number);
                    mParkingSpot.setOpen(isClicked);

                    iMyParkingSpotsActivity.updateSpot(mParkingSpot);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "You have to enter an Address!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.view_spot_delete:
                iMyParkingSpotsActivity.deleteSpot(mParkingSpot);
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
