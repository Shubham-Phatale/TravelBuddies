package com.example.travelbuddies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.travelbuddies.R;
import com.example.travelbuddies.databinding.ItemTripCardSmallBinding;
import com.example.travelbuddies.models.Trip;
import java.util.List;
import java.util.Locale;

public class TripMiniAdapter extends RecyclerView.Adapter<TripMiniAdapter.ViewHolder> {

    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    private final Context context;
    private final List<Trip> trips;
    private final OnTripClickListener listener;

    public TripMiniAdapter(Context context, List<Trip> trips, OnTripClickListener listener) {
        this.context = context;
        this.trips = trips;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripCardSmallBinding binding = ItemTripCardSmallBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.binding.tvTripTitle.setText(trip.getTitle());
        holder.binding.tvTripLocation.setText(trip.getLocation());
        holder.binding.tvTripPrice.setText(String.format(Locale.US, "$%,.0f", trip.getPrice()));
        Glide.with(context)
                .load(trip.getImageUrl())
                .placeholder(R.drawable.placeholder_trip_image)
                .centerCrop()
                .into(holder.binding.ivTripImage);
        holder.itemView.setOnClickListener(v -> listener.onTripClick(trip));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemTripCardSmallBinding binding;
        ViewHolder(ItemTripCardSmallBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
