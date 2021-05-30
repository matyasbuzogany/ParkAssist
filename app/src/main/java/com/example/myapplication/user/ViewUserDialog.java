package com.example.myapplication.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import com.example.myapplication.models.User;

public class ViewUserDialog extends DialogFragment implements View.OnClickListener {

    private EditText mEmail, mFirstName, mLastName, mPhoneNumber;
    private Button mCallUser, mCancel;

    User mUser;

    public static ViewUserDialog newInstance(User user) {
        ViewUserDialog dialog = new ViewUserDialog();

        Bundle args = new Bundle();
        args.putParcelable("user", user);
        dialog.setArguments(args);

        return dialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL;
        int theme = android.R.style.Theme_Holo_Dialog_MinWidth;
        setStyle(style, theme);

        mUser = getArguments().getParcelable("user");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_view_user, container, false);
        mEmail = view.findViewById(R.id.user_email);
//        mEmail.setKeyListener(null);
//        mEmail.setBackground(null);
        mEmail.setInputType(InputType.TYPE_NULL);
        mFirstName = view.findViewById(R.id.user_firstName);
//        mFirstName.setEnabled(false);
        mLastName = view.findViewById(R.id.user_lastName);
//        mLastName.setEnabled(false);
        mPhoneNumber = view.findViewById(R.id.user_phonenumber);
//        mPhoneNumber.setEnabled(false);
        mCallUser = view.findViewById(R.id.callUser);
        mCancel = view.findViewById(R.id.cancel);

        mCallUser.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        getDialog().setTitle("Owner of the Vehicle");

        setInitialProperties();

        return view;
    }

    private void setInitialProperties() {
        mEmail.setText(mUser.getEmail());
        mFirstName.setText(mUser.getFirstName());
        mLastName.setText(mUser.getLastName());
        mPhoneNumber.setText(mUser.getPhoneNumber());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case(R.id.cancel):
                getDialog().dismiss();
            case(R.id.callUser):
                getDialog().dismiss();
                String phoneNumber = mUser.getPhoneNumber();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
        }
    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        MainActivity.getActivity();
//    }
}
