package com.example.bitnest_android_2207086;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    Context context;
    ArrayList<Room> list;

    public RoomAdapter(Context context, ArrayList<Room> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.room_view, parent, false);
        return new RoomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = list.get(position);

        holder.roomNumber.setText("Room " + room.roomNumber);
        holder.type.setText(room.type);
        holder.price.setText("$" + room.price);

        if (room.isAvailable) {
            holder.status.setText("Available");
            holder.status.setTextColor(Color.parseColor("#009688")); // Green
        } else {
            holder.status.setText("Booked");
            holder.status.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomNumber, type, price, status;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNumber = itemView.findViewById(R.id.tvRoomNumber);
            type = itemView.findViewById(R.id.tvType);
            price = itemView.findViewById(R.id.tvPrice);
            status = itemView.findViewById(R.id.tvStatus);
        }
    }
}