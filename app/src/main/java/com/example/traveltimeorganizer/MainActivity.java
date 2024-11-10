package com.example.traveltimeorganizer;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.traveltimeorganizer.data.TripManager;
import com.example.traveltimeorganizer.data.models.Trip;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TripManager manager = new TripManager(this);
        // TODO Implement database and check for upcoming trips
        if (true) {
            Intent i = new Intent(this, NoInfoHomeActivity.class);
            startActivity(i);
            ActivityCompat.finishAffinity(this);
        }
        else {
            Intent i = new Intent(this, InfoHomeActivity.class);
            startActivity(i);
            ActivityCompat.finishAffinity(this);
        }
    }
}