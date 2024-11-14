package com.example.traveltimeorganizer.utils;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveltimeorganizer.MainActivity;
import com.example.traveltimeorganizer.NoInfoHomeActivity;
import com.example.traveltimeorganizer.R;
import com.example.traveltimeorganizer.data.TripManager;
import com.example.traveltimeorganizer.data.models.Trip;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {
    private final Activity parent;
    private List<Trip> trips;
    private int color;
    private TripManager manager;

    public TripAdapter(List<Trip> trips, Activity parent) {
        this.trips = trips;
        this.parent = parent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_trip_container, parent, false);
        color = ContextCompat.getColor(parent.getContext(), R.color.black);
        manager = new TripManager(parent.getContext());

        return new ViewHolder(view, manager, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trip current = this.trips.get(position);
        holder.currentId = current.getId();

        String fromText = current.getFromPlace() != null ? current.getFromPlace() : current.getFromLatitude() + ", " + current.getFromLongitude();
        String toText = current.getToPlace() != null ? current.getToPlace() : current.getToLatitude() + ", " + current.getToLongitude();
        holder.routeText.setText(String.format("%s - %s", fromText, toText));

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
                    holder.dayText.setVisibility(View.VISIBLE);
                    holder.execute_on.setVisibility(View.GONE);
                }
            }
        }

        else {
            holder.execute_on.setText(current.getExecuteOn());

            holder.dayText.setVisibility(View.GONE);
            holder.execute_on.setVisibility(View.VISIBLE);
        }

        holder.durationText.setText(Utils.formatTripTime(current.getTripTime()));
        holder.tripInfoSwitch.setChecked(current.isActive());

        holder.tripInfoSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            int id = current.getId();
            manager.setActiveStatus(id, isChecked);
            current.setActive(isChecked);
        });

    }

    public void onItemDeleted(int deletedId) {
        Trip deletedTrip = this.trips.stream().filter(trip -> trip.getId() == deletedId).collect(Collectors.toList()).get(0);
        int index = this.trips.indexOf(deletedTrip);
        this.trips.remove(deletedTrip);
        if (this.trips.isEmpty()) {
            Intent i = new Intent(parent, MainActivity.class);
            parent.startActivity(i);
            parent.finish();
        }
        else {
            this.notifyItemRemoved(index);
        }
    }

    @Override
    public int getItemCount() {
        return this.trips.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private int currentId = -1;
        private final TripAdapter adapter;
        private final TripManager manager;
        private final TextView routeText;
        private final TextView execute_on;
        private final LinearLayout dayText;
        private final TextView monday;
        private final TextView tuesday;
        private final TextView wednesday;
        private final TextView thursday;
        private final TextView friday;
        private final TextView saturday;
        private final TextView sunday;
        private final TextView durationText;
        private final SwitchCompat tripInfoSwitch;

        public ViewHolder(@NonNull View itemView, TripManager manager, TripAdapter adapter) {
            super(itemView);
            this.manager = manager;
            this.adapter = adapter;
            routeText = itemView.findViewById(R.id.routeText);
            execute_on = itemView.findViewById(R.id.execute_on);
            dayText = itemView.findViewById(R.id.dayText);
            monday = itemView.findViewById(R.id.mon);
            tuesday = itemView.findViewById(R.id.tue);
            wednesday = itemView.findViewById(R.id.wed);
            thursday = itemView.findViewById(R.id.thu);
            friday = itemView.findViewById(R.id.fri);
            saturday = itemView.findViewById(R.id.sat);
            sunday = itemView.findViewById(R.id.sun);
            durationText = itemView.findViewById(R.id.durationText);
            tripInfoSwitch = itemView.findViewById(R.id.tripInfoSwitch);
            itemView.setOnCreateContextMenuListener(this);
        }


        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuInflater inflater = new MenuInflater(view.getContext());
            inflater.inflate(R.menu.trip_container_menu, contextMenu);
            contextMenu.getItem(0).setOnMenuItemClickListener(menuItem -> {
                new AlertDialog.Builder(view.getContext())
                        .setMessage(R.string.delete_trip_popup_text)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.yes, (dialog, button) -> {
                            manager.deleteTrip(currentId);
                            adapter.onItemDeleted(currentId);
                        })
                        .setNegativeButton(R.string.no, (dialog, button) -> {
                            dialog.cancel();
                        })
                        .show();
                return false;
            });
        }
    }
}
