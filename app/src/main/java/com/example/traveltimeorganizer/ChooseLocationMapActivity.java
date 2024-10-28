package com.example.traveltimeorganizer;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;

public class ChooseLocationMapActivity extends AppCompatActivity {
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_choose_location_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.initMap();

//        Bundle bundle = getIntent().getExtras();
//        if (bundle != null) {
//            int a = bundle.getInt(Integer.toString(R.id.addTripFromButton));
//            int b = bundle.getInt(Integer.toString(R.id.addTripToButton));
//            int c = 0;
//        }

        SwitchCompat mapViewSwitch1 = findViewById(R.id.mapViewSwitch1);
        mapViewSwitch1.setOnCheckedChangeListener((view, isChecked) -> {
            // TODO: Implement
        });

        findViewById(R.id.confirmLocationButton).setOnClickListener(view -> {
            // TODO: Implement
        });

    }

    private void initMap() {
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        this.map = findViewById(R.id.map);
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        this.map.setMultiTouchControls(true);

        GeoPoint centerOfBgCoordinates = new GeoPoint(42.76, 25.23);

        IMapController mapController = this.map.getController();
        mapController.setZoom(8.0);
        mapController.setCenter(centerOfBgCoordinates);
    }
}