package com.example.bitnest_android_2207086;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewGuestAdapter extends RecyclerView.Adapter<ViewGuestAdapter.ViewHolder> {

    Context context;
    ArrayList<Map<String, Object>> list;
    HashMap<String, String> roomMap;

    public ViewGuestAdapter(Context context, ArrayList<Map<String, Object>> list, HashMap<String, String> roomMap) {
        this.context = context;
        this.list = list;
        this.roomMap = roomMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_guest_view, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> guest = list.get(position);

        holder.name.setText((String) guest.get("name"));
        holder.phone.setText("Phone: " + guest.get("phone"));
        holder.dates.setText("Check-in: " + guest.get("checkIn") + "\nCheck-out: " + guest.get("checkOut"));


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

        if (roomNumbers.length() > 0) {
            holder.roomHeader.setText("Room " + roomNumbers.toString());
        } else {
            holder.roomHeader.setText("No Room Assigned");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView roomHeader, name, phone, dates;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomHeader = itemView.findViewById(R.id.tvRoomHeader);
            name = itemView.findViewById(R.id.tvGuestName);
            phone = itemView.findViewById(R.id.tvPhone);
            dates = itemView.findViewById(R.id.tvCheckDates);
        }
    }
}