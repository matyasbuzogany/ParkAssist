//package com.example.myapplication;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//
//import java.util.List;
//
//
//public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
//
//    List<String> titles;
//    List<Integer> images;
//    LayoutInflater inflater;
//
//    public Adapter(Context context, List<String> titles, List<Integer> images) {
//        this.titles = titles;
//        this.images = images;
//        this.inflater = LayoutInflater.from(context);
//    }
//
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = inflater.inflate(R.layout.custom_grid_layout, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.textView.setText(titles.get(position));
//        holder.imageView.setImageResource(images.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return titles.size();
//    }
//
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        TextView textView;
//        ImageView imageView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            textView = itemView.findViewById(R.id.textViewCustom);
//            imageView = itemView.findViewById(R.id.imageViewCustom);
//
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(), "Clicked -> " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//}
