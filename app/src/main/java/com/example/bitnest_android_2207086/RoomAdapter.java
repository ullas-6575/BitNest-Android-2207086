package com.example.bitnest_android_2207086;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    Context context;
    ArrayList<Room> list;
    private HashSet<Integer> selectedPositions = new HashSet<>();
    private OnRoomListener onRoomListener;

    public interface OnRoomListener {
        void onRoomClick(int count);
    }

    public RoomAdapter(Context context, ArrayList<Room> list, OnRoomListener onRoomListener) {
        this.context = context;
        this.list = list;
        this.onRoomListener = onRoomListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.room_view, parent, false);
        return new RoomViewHolder(v, onRoomListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = list.get(position);

        holder.roomNumber.setText("Room " + room.roomNumber);
        holder.type.setText(room.type);
        holder.price.setText("$" + room.price);

        if (room.isAvailable) {
            holder.status.setText("Available");
            holder.status.setTextColor(Color.parseColor("#009688"));
        } else {
            holder.status.setText("Booked");
            holder.status.setTextColor(Color.RED);
        }

        if (selectedPositions.contains(position)) {
            holder.cardView.setCardBackgroundColor(Color.parseColor("#E0F2F1"));
        } else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (room.isAvailable) {
                if (selectedPositions.contains(position)) {
                    selectedPositions.remove(position);
                } else {
                    selectedPositions.add(position);
                }
                notifyItemChanged(position);
                onRoomListener.onRoomClick(selectedPositions.size());
            } else {
                Toast.makeText(context, "This room is already booked!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public ArrayList<Room> getSelectedRooms() {
        ArrayList<Room> selectedRooms = new ArrayList<>();
        for (int index : selectedPositions) {
            selectedRooms.add(list.get(index));
        }
        return selectedRooms;
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView roomNumber, type, price, status;
        CardView cardView;

        public RoomViewHolder(@NonNull View itemView, OnRoomListener listener) {
            super(itemView);
            roomNumber = itemView.findViewById(R.id.tvRoomNumber);
            type = itemView.findViewById(R.id.tvType);
            price = itemView.findViewById(R.id.tvPrice);
            status = itemView.findViewById(R.id.tvStatus);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}