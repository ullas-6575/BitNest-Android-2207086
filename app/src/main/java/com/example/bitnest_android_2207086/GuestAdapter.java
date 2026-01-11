package com.example.bitnest_android_2207086;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuestAdapter extends RecyclerView.Adapter<GuestAdapter.GuestViewHolder> {

    Context context;
    ArrayList<Map<String, Object>> list;
    HashMap<String, String> roomMap;
    OnCheckoutListener listener;

    public interface OnCheckoutListener {
        void onCheckoutClick(Map<String, Object> guestData);
    }

    public GuestAdapter(Context context, ArrayList<Map<String, Object>> list, HashMap<String, String> roomMap, OnCheckoutListener listener) {
        this.context = context;
        this.list = list;
        this.roomMap = roomMap;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_guest, parent, false);
        return new GuestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        Map<String, Object> guest = list.get(position);

        holder.name.setText("Name: " + guest.get("name"));
        holder.phone.setText("Phone: " + guest.get("phone"));

        ArrayList<String> roomIds = (ArrayList<String>) guest.get("bookedRooms");
        StringBuilder roomNumbers = new StringBuilder();

        if (roomIds != null) {
            for (String id : roomIds) {
                if (roomMap.containsKey(id)) {
                    if (roomNumbers.length() > 0) roomNumbers.append(", ");
                    roomNumbers.append(roomMap.get(id));
                }
            }
        }

        holder.rooms.setText("Rooms: " + roomNumbers.toString());

        holder.btnCheckout.setOnClickListener(v -> listener.onCheckoutClick(guest));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class GuestViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, rooms;
        Button btnCheckout;

        public GuestViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvGuestName);
            phone = itemView.findViewById(R.id.tvPhone);
            rooms = itemView.findViewById(R.id.tvRoomInfo);
            btnCheckout = itemView.findViewById(R.id.btnCheckoutAction);
        }
    }
}