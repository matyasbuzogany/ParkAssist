package com.example.myapplication.parkingspot;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.ParkingSpot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ParkingSpotRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ParkingSpot> mSpots = new ArrayList<>();
    private IMyParkingSpotsActivity mIMyParkingSpotsActivity;
    private Context mContext;
    private int mSelectedSpot;

    public ParkingSpotRecyclerViewAdapter(Context context, ArrayList<ParkingSpot> spots) {
        mContext = context;
        mSpots = spots;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parkingspot_list_item, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            String displayAddress = mSpots.get(position).getAddress();
            String displayCountry = mSpots.get(position).getCountry();
            String displayCity = mSpots.get(position).getCity();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
            String date = sdf.format(mSpots.get(position).getTimestamp());

            ((ViewHolder)holder).address.setText(displayAddress);
            ((ViewHolder)holder).country.setText(displayCountry);
            ((ViewHolder)holder).city.setText(displayCity);
            ((ViewHolder)holder).timestamp.setText(date);

        }
    }




    @Override
    public int getItemCount() {
        return mSpots.size();
    }



    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mIMyParkingSpotsActivity = (IMyParkingSpotsActivity) mContext;
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView address, city, country, timestamp;

        public ViewHolder(View  itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            country = itemView.findViewById(R.id.country);
            city = itemView.findViewById(R.id.city);
            timestamp = itemView.findViewById(R.id.timestamp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSelectedSpot = getAdapterPosition();
            mIMyParkingSpotsActivity.onSpotSelected(mSpots.get(mSelectedSpot));
        }
    }
}
