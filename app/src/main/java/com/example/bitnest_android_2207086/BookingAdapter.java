package com.example.bitnest_android_2207086;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private ArrayList<Booking> bookingList;

    public BookingAdapter(ArrayList<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.tvGuestName.setText(booking.name);
        holder.tvDates.setText("In: " + booking.checkIn + " - Out: " + booking.checkOut);


        if (booking.bookedRooms != null && !booking.bookedRooms.isEmpty()) {
            fetchRoomNumbers(booking.bookedRooms, holder.tvRoomNumber);
        } else {
            holder.tvRoomNumber.setText("No Rooms Assigned");
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    private void fetchRoomNumbers(ArrayList<String> roomIds, TextView textView) {
        DatabaseReference roomRef = FirebaseDatabase.getInstance("https://bitnest-auth-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("rooms");

        roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder roomsStr = new StringBuilder();
                for (String id : roomIds) {
                    if (snapshot.hasChild(id)) {
                        String num = snapshot.child(id).child("roomNumber").getValue(String.class);
                        if (roomsStr.length() > 0) roomsStr.append(", ");
                        roomsStr.append(num);
                    }
                }
                textView.setText("Rooms: " + roomsStr.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvGuestName, tvDates, tvRoomNumber;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGuestName = itemView.findViewById(R.id.tvGuestName);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvRoomNumber = itemView.findViewById(R.id.tvRoomNumber);
        }
    }
}