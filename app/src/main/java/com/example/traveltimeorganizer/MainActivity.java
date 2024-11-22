package com.example.traveltimeorganizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.traveltimeorganizer.data.TripManager;
import com.example.traveltimeorganizer.data.models.Trip;
import com.example.traveltimeorganizer.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TripManager manager = new TripManager(this);
        ArrayList<Trip> trips = manager.getTrips();
        Intent i;
        if (trips.isEmpty()) {
            i = new Intent(this, NoInfoHomeActivity.class);
        }
        else {
            i = new Intent(this, InfoHomeActivity.class);
            i.putExtra(Constants.TABLE_TRIPS_NAME, trips);
        }
        startActivity(i);
        ActivityCompat.finishAffinity(this);
    }
}