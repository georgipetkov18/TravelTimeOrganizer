package com.example.traveltimeorganizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.util.GeoPoint;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddTripDetailsActivity extends AppCompatActivity {
    private Date tripDateTime;
    private EditText addTripDateInput;
    private EditText addTripMinEarlierInput;
    private ActivityResultLauncher<Intent> activityResultLauncherFrom;
    private ActivityResultLauncher<Intent> activityResultLauncherTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_trip_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        this.addTripDateInput = findViewById(R.id.addTripDate);
        this.addTripMinEarlierInput = findViewById(R.id.addTripMinEarlier);

        this.setListeners();
        this.registerActivityResults();
    }


    private @NonNull DatePickerDialog getDatePickerDialog() {
        Calendar now = Calendar.getInstance();

        return new DatePickerDialog(this, (datePicker, year, month, day) -> {
            TimePickerDialog timePickerDialog = getTimePickerDialog(now, day, month, year);
            timePickerDialog.show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
    }

    private @NonNull TimePickerDialog getTimePickerDialog(Calendar now, int day, int month, int year) {
        return new TimePickerDialog(this, (timePicker, hour, minute) -> {
            Calendar chosenDate = Calendar.getInstance();
            chosenDate.set(year, month, day, hour, minute);
            this.tripDateTime = chosenDate.getTime();

            String fullDateTime = String.format(Locale.getDefault(), "%d.%d.%d %d:%d", day, month, year, hour, minute);

            this.addTripDateInput.setText(fullDateTime);
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
    }

    private void setListeners() {
        this.addTripDateInput.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = getDatePickerDialog();
            datePickerDialog.show();
        });

        SwitchCompat addTripOnTimeSwitch = findViewById(R.id.addTripOnTimeSwitch);
        addTripOnTimeSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            this.addTripMinEarlierInput.setEnabled(!isChecked);
        });

        findViewById(R.id.addTripFromButton).setOnClickListener(view -> {
            Intent i = new Intent(this, ChooseLocationMapActivity.class);
            this.activityResultLauncherFrom.launch(i);
        });

        findViewById(R.id.addTripToButton).setOnClickListener(view -> {
            Intent i = new Intent(this, ChooseLocationMapActivity.class);
            this.activityResultLauncherTo.launch(i);
        });
    }

    private void registerActivityResults() {
        this.activityResultLauncherFrom = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                this.updateTripInput(R.id.addTripFromInput, result);
            }
        });

        this.activityResultLauncherTo = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                this.updateTripInput(R.id.addTripToInput, result);
            }
        });
    }

    private void updateTripInput(int id, ActivityResult result) {
        if (result.getResultCode() != Activity.RESULT_OK) {
            return;
        }

        Intent data = result.getData();
        if (data == null) {
            return;
        }

        Bundle b = data.getExtras();
        if (b == null) {
            return;
        }

        double lat = b.getDouble("latitude");
        double lon = b.getDouble("longitude");
        String text = lat + " " + lon;
        EditText input = findViewById(id);
        input.setText(text);
    }
}