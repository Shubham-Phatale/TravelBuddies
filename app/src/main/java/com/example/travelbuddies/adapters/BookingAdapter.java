package com.example.travelbuddies.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.travelbuddies.activities.TransportActivity;
import com.example.travelbuddies.database.DBHelper;
import com.example.travelbuddies.databinding.ItemBookingBinding;
import com.example.travelbuddies.models.Booking;
import com.example.travelbuddies.models.Trip;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingVH> {

    public interface OnBookingCancelledListener {
        void onBookingCancelled();
    }

    private final Context context;
    private final List<Booking> bookingList;
    private final DBHelper dbHelper;
    private final OnBookingCancelledListener cancelListener;

    public BookingAdapter(Context context, List<Booking> bookingList, DBHelper dbHelper,
                          OnBookingCancelledListener cancelListener) {
        this.context = context;
        this.bookingList = bookingList;
        this.dbHelper = dbHelper;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public BookingVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookingVH(ItemBookingBinding.inflate(
                LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookingVH holder, int position) {
        Booking booking = bookingList.get(position);
        Trip trip = dbHelper.getTripById(booking.getTripId());

        if (trip != null) {
            holder.b.tvBookingTripTitle.setText(trip.getTitle());
            double total = trip.getPrice() * booking.getNumberOfPeople();
            holder.b.tvBookingTotal.setText(String.format(Locale.US, "$%,.0f", total));
        } else {
            holder.b.tvBookingTripTitle.setText("Trip not found");
            holder.b.tvBookingTotal.setText("—");
        }

        holder.b.tvBookingDate.setText(booking.getBookingDate());
        int people = booking.getNumberOfPeople();
        holder.b.tvBookingPeople.setText(people + " Traveler" + (people > 1 ? "s" : ""));

        String transportMode = dbHelper.getTransportModeByBookingId(booking.getBookingId());
        if (transportMode != null) {
            holder.b.tvTransportMode.setText("Transport: " + transportMode);
            holder.b.btnManageTransport.setText("Change");
        } else {
            holder.b.tvTransportMode.setText("No transport selected");
            holder.b.btnManageTransport.setText("Add");
        }

        holder.b.btnManageTransport.setOnClickListener(v -> {
            if (trip == null) return;
            Intent intent = new Intent(context, TransportActivity.class);
            intent.putExtra("bookingId", booking.getBookingId());
            intent.putExtra("trip", trip);
            intent.putExtra("travelDate", booking.getBookingDate());
            intent.putExtra("existingMode", transportMode);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.b.btnCancelBooking.setOnClickListener(v -> {
            String tripName = trip != null ? trip.getTitle() : "this booking";
            new AlertDialog.Builder(context)
                    .setTitle("Cancel Booking")
                    .setMessage("Are you sure you want to cancel \"" + tripName + "\"? This cannot be undone.")
                    .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                        dbHelper.cancelBooking(booking.getBookingId());
                        if (cancelListener != null) cancelListener.onBookingCancelled();
                    })
                    .setNegativeButton("Keep", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() { return bookingList.size(); }

    static class BookingVH extends RecyclerView.ViewHolder {
        final ItemBookingBinding b;
        BookingVH(ItemBookingBinding b) { super(b.getRoot()); this.b = b; }
    }
}
