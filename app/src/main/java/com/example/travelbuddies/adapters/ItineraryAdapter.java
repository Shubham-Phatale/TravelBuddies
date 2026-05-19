package com.example.travelbuddies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.travelbuddies.databinding.ItemItineraryDayBinding;
import com.example.travelbuddies.models.ItineraryDay;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.ItinVH> {

    private final List<ItineraryDay> days;
    private final Set<Integer> expanded = new HashSet<>();

    public ItineraryAdapter(List<ItineraryDay> days) {
        this.days = days;
    }

    @NonNull
    @Override
    public ItinVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItinVH(ItemItineraryDayBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItinVH holder, int position) {
        ItineraryDay day = days.get(position);
        boolean isExpanded = expanded.contains(position);

        holder.b.tvDayBadge.setText("Day\n" + day.getDayNumber());
        holder.b.tvItinTitle.setText(day.getTitle());
        holder.b.tvItinDesc.setText(day.getDescription());
        holder.b.tvActivities.setText(day.getActivities());

        holder.b.llActivities.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.b.dividerItin.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.b.ivExpandArrow.setRotation(isExpanded ? 90f : -90f);

        holder.b.llItineraryHeader.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_ID) return;
            boolean nowExpanded;
            if (expanded.contains(pos)) {
                expanded.remove(pos);
                nowExpanded = false;
            } else {
                expanded.add(pos);
                nowExpanded = true;
            }
            animateArrow(holder.b.ivExpandArrow, nowExpanded);
            notifyItemChanged(pos);
        });
    }

    private void animateArrow(View arrow, boolean expand) {
        float from = expand ? -90f : 90f;
        float to = expand ? 90f : -90f;
        RotateAnimation rotate = new RotateAnimation(from, to,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(200);
        rotate.setFillAfter(true);
        arrow.startAnimation(rotate);
    }

    @Override
    public int getItemCount() { return days.size(); }

    static class ItinVH extends RecyclerView.ViewHolder {
        final ItemItineraryDayBinding b;
        ItinVH(ItemItineraryDayBinding b) { super(b.getRoot()); this.b = b; }
    }
}
