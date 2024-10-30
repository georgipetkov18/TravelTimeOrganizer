package com.example.traveltimeorganizer;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

import com.example.traveltimeorganizer.utils.Constants;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;

public class ChooseLocationMapActivity extends AppCompatActivity {
    private MapView map;
    private GeoPoint currentPoint;

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

        SwitchCompat mapViewSwitch1 = findViewById(R.id.mapViewSwitch1);
        mapViewSwitch1.setOnCheckedChangeListener((view, isChecked) -> {
            // TODO: Implement
        });

        findViewById(R.id.confirmLocationButton).setOnClickListener(view -> {
            if (this.currentPoint != null) {
                Intent i = new Intent();
                i.putExtra(Constants.LATITUDE, this.currentPoint.getLatitude());
                i.putExtra(Constants.LONGITUDE, this.currentPoint.getLongitude());
                setResult(Activity.RESULT_OK, i);
            }
            this.finish();
        });
    }

    private void initMap() {
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
        Configuration.getInstance().load(getApplicationContext(), PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(gfgPolicy);

        this.map = findViewById(R.id.map);
        this.map.setTileSource(TileSourceFactory.MAPNIK);
        this.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        this.map.setMultiTouchControls(true);

        IMapController mapController = this.map.getController();
        mapController.setZoom(8.0);
        mapController.setCenter(Constants.CENTER_OF_BG);

        MapEventsOverlay eventsOverlay = getMapEventsOverlay();
        this.map.getOverlays().add(eventsOverlay);
    }

    private @NonNull MapEventsOverlay getMapEventsOverlay() {
        MapEventsReceiver receiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                clearMarkers();
                Marker marker = new Marker(map);
                marker.setPosition(p);
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                map.getOverlays().add(marker);
                currentPoint = p;
                map.postInvalidate();
                return true;
            }
        };

        return new MapEventsOverlay(receiver);
    }

    private void clearMarkers() {
        for (int i = 0; i < map.getOverlays().size(); i++) {
            Overlay overlay = map.getOverlays().get(i);
            if (overlay instanceof Marker) {
                map.getOverlays().remove(overlay);
            }
        }
    }
}