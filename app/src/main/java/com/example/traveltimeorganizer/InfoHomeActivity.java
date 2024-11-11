package com.example.traveltimeorganizer;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.traveltimeorganizer.data.models.Trip;
import com.example.traveltimeorganizer.utils.Constants;

import java.util.ArrayList;
import java.util.Map;

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

        if (trips == null) {
            return;
        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        for (int i = 0; i < trips.size(); i++) {
            SingleTripContainer fragment = SingleTripContainer.newInstance(i);
        }
        transaction.commit();
    }
}