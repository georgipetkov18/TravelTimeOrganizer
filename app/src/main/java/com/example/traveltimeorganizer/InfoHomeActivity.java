package com.example.traveltimeorganizer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
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

        ArrayList<Trip> trips = (ArrayList<Trip>)getIntent().getExtras().getSerializable(Constants.TABLE_TRIPS_NAME);
        RecyclerView recycler = findViewById(R.id.trip_recycler);
        recycler.setLayoutManager(new LinearLayoutManager((this)));
        recycler.setAdapter(new TripAdapter(trips));
    }
}