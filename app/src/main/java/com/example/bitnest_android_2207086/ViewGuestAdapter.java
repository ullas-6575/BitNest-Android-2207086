package com.example.bitnest_android_2207086;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ViewGuestAdapter extends RecyclerView.Adapter<ViewGuestAdapter.GuestViewHolder> {

    Context context;
    ArrayList<Map<String, String>> list;

    public ViewGuestAdapter(Context context, ArrayList<Map<String, String>> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_guest, parent, false);
        return new GuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, int position) {
        Map<String, String> currentItem = list.get(position);

        holder.tvRoom.setText("Room: " + currentItem.get("roomNum"));
        holder.tvName.setText(currentItem.get("name"));
        holder.tvPhone.setText(currentItem.get("phone"));
        holder.tvDates.setText("From: " + currentItem.get("checkIn") + "  To: " + currentItem.get("checkOut"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class GuestViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRoom, tvPhone, tvDates;

        public GuestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvGuestName);
            tvRoom = itemView.findViewById(R.id.tvRoomNum);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvDates = itemView.findViewById(R.id.tvDates);
        }
    }
}