package com.example.traveltimeorganizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.traveltimeorganizer.data.models.Trip;
import com.example.traveltimeorganizer.utils.Constants;
import com.example.traveltimeorganizer.utils.TripAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class InfoHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }

        ArrayList<Trip> trips = (ArrayList<Trip>) getIntent().getExtras().getSerializable(Constants.TABLE_TRIPS_NAME);
        RecyclerView recycler = findViewById(R.id.trip_recycler);
        recycler.setLayoutManager(new LinearLayoutManager((this)));
        recycler.setAdapter(new TripAdapter(trips, this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        menu.getItem(0).setOnMenuItemClickListener(menuItem -> {
            Intent i = new Intent(this, AddTripDetailsActivity.class);
            startActivity(i);
            return true;
        });
        return true;
    }
}