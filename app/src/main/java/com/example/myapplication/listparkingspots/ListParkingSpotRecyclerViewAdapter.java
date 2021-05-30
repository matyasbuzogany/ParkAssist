package com.example.myapplication.listparkingspots;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.ParkingSpot;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class ListParkingSpotRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<ParkingSpot> mSpots = new ArrayList<>();
    private Context mContext;
    private int mSelectedSpot;
    private IListParkingSpotsActivity iListParkingSpotsActivity;


    public ListParkingSpotRecyclerViewAdapter(Context context, ArrayList<ParkingSpot> spots) {
        mContext = context;
        mSpots = spots;
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder holder;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listparkingspot_list_item, parent, false);
        holder = new ViewHolder(view);
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            Location currentLocation = new Location("Current");
            currentLocation.setLatitude(46.773018);
            currentLocation.setLongitude(23.595214);

            Location spotLocation = new Location("Parking Spot");
            spotLocation.setLatitude(Double.parseDouble(mSpots.get(position).getLatitude()));
            spotLocation.setLongitude(Double.parseDouble(mSpots.get(position).getLongitude()));

            double distance = currentLocation.distanceTo(spotLocation)/1000;
            String displayAddress = mSpots.get(position).getAddress();

            ((ViewHolder)holder).address.setText(displayAddress);

            String distanceString = String.valueOf(distance);

            if (distance < 10) {
                String displayDistance = distanceString.charAt(0) + ", " + distanceString.charAt(2) + " km away";
                ((ViewHolder)holder).distance.setText(displayDistance);
            } else if (distance  > 10 && distance < 100) {
                String displayDistance = distanceString.substring(0,1) + ", " + distanceString.charAt(3) + " km away";
                ((ViewHolder)holder).distance.setText(displayDistance);
            } else {
                String displayDistance = distanceString.substring(0,3) + " km away";;
                ((ViewHolder)holder).distance.setText(displayDistance);
            }
        }
    }




    @Override
    public int getItemCount() {
        return mSpots.size();
    }



    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        iListParkingSpotsActivity = (IListParkingSpotsActivity) mContext;
    }



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView address, distance;
        Button reserveButton;

        public ViewHolder(View  itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.listparkingspots_address);
            distance = itemView.findViewById(R.id.listparkingspots_distance);
            reserveButton = itemView.findViewById(R.id.button_reserve);

            reserveButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mSelectedSpot = getAdapterPosition();
            iListParkingSpotsActivity.onReserveButtonClicked(mSpots.get(mSelectedSpot));
        }
    }
}
