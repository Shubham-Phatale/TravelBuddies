package com.example.travelbuddies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.travelbuddies.databinding.ItemBuddyBinding;
import java.util.List;

public class BuddyAdapter extends RecyclerView.Adapter<BuddyAdapter.BuddyVH> {

    public static class Buddy {
        public final String name;
        public final String city;
        public final String avatarUrl;
        public Buddy(String name, String city, String avatarUrl) {
            this.name = name; this.city = city; this.avatarUrl = avatarUrl;
        }
    }

    private final Context context;
    private final List<Buddy> buddies;

    public BuddyAdapter(Context context, List<Buddy> buddies) {
        this.context = context;
        this.buddies = buddies;
    }

    @NonNull
    @Override
    public BuddyVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BuddyVH(ItemBuddyBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BuddyVH holder, int position) {
        Buddy b = buddies.get(position);
        holder.binding.tvBuddyName.setText(b.name);
        holder.binding.tvBuddyCity.setText(b.city);
        Glide.with(context).load(b.avatarUrl).circleCrop().into(holder.binding.ivBuddyAvatar);
    }

    @Override
    public int getItemCount() { return buddies.size(); }

    static class BuddyVH extends RecyclerView.ViewHolder {
        final ItemBuddyBinding binding;
        BuddyVH(ItemBuddyBinding b) { super(b.getRoot()); binding = b; }
    }
}
