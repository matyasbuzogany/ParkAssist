package com.example.myapplication.car;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.R;
import com.example.myapplication.models.Car;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CarRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Car> mCars = new ArrayList<>();
    private ICarActivity mICarActivity;
    private Context mContext;
    private int mSelectedCarIndex;

    public CarRecyclerViewAdapter(Context context, ArrayList<Car> cars) {
        mCars = cars;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.car_list_item, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }




    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            String displayTitle = mCars.get(position).getBrand();
            String displayNumberplate = mCars.get(position).getNumberplate();
            String displayYear = mCars.get(position).getProductionYear();
            String displayColor = mCars.get(position).getColor();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
            String date = sdf.format(mCars.get(position).getTimestamp());

            ((ViewHolder)holder).title.setText(displayTitle);
            ((ViewHolder)holder).numberplate.setText(displayNumberplate);
            ((ViewHolder)holder).timestamp.setText(date);
            ((ViewHolder)holder).year.setText(displayYear);
            ((ViewHolder)holder).color.setText(displayColor);
        }
    }




    @Override
    public int getItemCount() {
        return mCars.size();
    }



    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mICarActivity = (ICarActivity) mContext;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView title, numberplate, year, color, timestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            numberplate = itemView.findViewById(R.id.numberplate);
            timestamp = itemView.findViewById(R.id.timestamp);
            year = itemView.findViewById(R.id.year);
            color = itemView.findViewById(R.id.color);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSelectedCarIndex = getAdapterPosition();
            mICarActivity.onCarSelected(mCars.get(mSelectedCarIndex));
        }
    }
}
