package com.example.myapplication;

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

import com.example.myapplication.car.ICarActivity;

public class EnterNumberplateDialog extends DialogFragment implements View.OnClickListener {

    private EditText mNumberplate;
    private Button mSearch, mCancel;

    IMainActivity iMainActivity;

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
        View view = inflater.inflate(R.layout.dialog_enter_numberplate, container, false);
        mNumberplate = view.findViewById(R.id.enter_numberplate);
        mSearch = view.findViewById(R.id.enter_numberplate_search);
        mCancel = view.findViewById(R.id.enter_numberplate_cancel);

        mSearch.setOnClickListener(this);
        mCancel.setOnClickListener(this);

        getDialog().setTitle("Serach for User with Numberplate");

        return view;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.enter_numberplate_search:
                String numberplate = mNumberplate.getText().toString().trim();

                if (!numberplate.equals("")) {
                    iMainActivity.getCarForNumberplate(numberplate);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "You have to enter a Numberplate!", Toast.LENGTH_SHORT);
                }
            case R.id.enter_numberplate_cancel:
                getDialog().dismiss();
                break;
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        iMainActivity = (IMainActivity) getActivity();
    }
}
