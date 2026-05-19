package com.example.travelbuddies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.travelbuddies.R;
import com.example.travelbuddies.databinding.ItemTripCardBinding; // Import ViewBinding
import com.example.travelbuddies.models.Trip;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    // An interface to let the Fragment handle clicks
    public interface OnTripClickListener {
        void onTripClick(Trip trip);
    }

    private final Context context;
    private final List<Trip> tripList;
    private final OnTripClickListener clickListener;

    public TripAdapter(Context context, List<Trip> tripList, OnTripClickListener clickListener) {
        this.context = context;
        this.tripList = tripList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // âœ… Inflate the CORRECT layout using ViewBinding
        ItemTripCardBinding binding = ItemTripCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TripViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.bind(trip, clickListener);
    }

    public void updateList(List<Trip> newList) {
        tripList.clear();
        tripList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    // ViewHolder now uses ViewBinding
    public class TripViewHolder extends RecyclerView.ViewHolder {
        private final ItemTripCardBinding binding;

        public TripViewHolder(ItemTripCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Trip trip, final OnTripClickListener listener) {
            // Bind data to the new layout's views
            binding.tvTripTitle.setText(trip.getTitle());
            binding.tvTripLocation.setText(trip.getLocation());
            binding.tvTripPrice.setText("$" + String.format(Locale.US, "%,.0f", trip.getPrice()));
            binding.tvTripDuration.setText(trip.getDuration());
            binding.tvTripRating.setText(String.valueOf(trip.getRating()));

            Glide.with(context)
                    .load(trip.getImageUrl())
                    .placeholder(R.drawable.placeholder_trip_image)
                    .into(binding.ivTripImage);

            itemView.setOnClickListener(v -> listener.onTripClick(trip));
        }
    }
}