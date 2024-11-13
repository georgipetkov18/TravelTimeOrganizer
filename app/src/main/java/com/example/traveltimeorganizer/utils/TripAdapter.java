package com.example.traveltimeorganizer.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveltimeorganizer.R;
import com.example.traveltimeorganizer.data.models.Trip;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private final List<Trip> trips;
    private int color;

    public TripAdapter(List<Trip> trips) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_trip_container, parent, false);
        color = ContextCompat.getColor(parent.getContext(), R.color.black);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip current = this.trips.get(position);

        String fromText = current.getFromPlace() != null ? current.getFromPlace() : current.getFromLatitude() + ", " + current.getFromLongitude();
        String toText = current.getToPlace() != null ? current.getToPlace() : current.getToLatitude() + ", " + current.getToLongitude();
        holder.routeText.setText(String.format("%s - %s", fromText, toText));

        // TODO: Fix
        if (current.getRepeatOnDay() != null) {
            for (Map.Entry<Integer, String> entry : Constants.DAY_OF_WEEK_INDEX_MAP.entrySet()) {
                TextView currentView = null;
                switch (entry.getKey()) {
                    case 2:
                        currentView = holder.monday;
                        break;

                    case 4:
                        currentView = holder.tuesday;
                        break;

                    case 8:
                        currentView = holder.wednesday;
                        break;

                    case 16:
                        currentView = holder.thursday;
                        break;

                    case 32:
                        currentView = holder.friday;
                        break;

                    case 64:
                        currentView = holder.saturday;
                        break;

                    case 128:
                        currentView = holder.sunday;
                        break;
                }

                if (currentView != null && (current.getRepeatOnDay() & entry.getKey()) == entry.getKey()) {
                    currentView.setTextColor(color);
                }
            }
        }


        holder.durationText.setText(Utils.formatTripTime(current.getTripTime()));
        holder.tripInfoSwitch.setChecked(current.isActive());

    }

    @Override
    public int getItemCount() {
        return this.trips.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView routeText;
        private final TextView monday;
        private final TextView tuesday;
        private final TextView wednesday;
        private final TextView thursday;
        private final TextView friday;
        private final TextView saturday;
        private final TextView sunday;
        private final TextView durationText;
        private final SwitchCompat tripInfoSwitch;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            routeText = itemView.findViewById(R.id.routeText);
            monday = itemView.findViewById(R.id.mon);
            tuesday = itemView.findViewById(R.id.tue);
            wednesday = itemView.findViewById(R.id.wed);
            thursday = itemView.findViewById(R.id.thu);
            friday = itemView.findViewById(R.id.fri);
            saturday = itemView.findViewById(R.id.sat);
            sunday = itemView.findViewById(R.id.sun);
            durationText = itemView.findViewById(R.id.durationText);
            tripInfoSwitch = itemView.findViewById(R.id.tripInfoSwitch);
        }
    }
}
