package com.example.traveltimeorganizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;

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

import com.example.traveltimeorganizer.utils.Constants;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AddTripDetailsActivity extends AppCompatActivity {
    private Date tripDateTime;
    private EditText addTripDateInput;
    private EditText addTripMinEarlierInput;
    private ActivityResultLauncher<Intent> activityResultLauncherFrom;
    private ActivityResultLauncher<Intent> activityResultLauncherTo;
    private MaterialButtonToggleGroup group;
    private LinearLayout dayOfWeekPickerRow;
    private LinearLayout selectTimeRow1;
    private GeocoderNominatim geocoder;

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
        this.group = findViewById(R.id.toggleButtonGroup);
        this.dayOfWeekPickerRow = findViewById(R.id.dayOfWeekPickerRow);
        this.selectTimeRow1 = findViewById(R.id.selectTimeRow1);
        for ( int i = 0; i < this.selectTimeRow1.getChildCount();i++ ){
            this.selectTimeRow1.getChildAt(i).setEnabled(false);
        }
        this.setListeners();
        this.registerActivityResults();

        this.geocoder = new GeocoderNominatim(Locale.getDefault(), getText(R.string.app_name).toString());
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

        findViewById(R.id.saveTripButton).setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.save_trip_popup_text)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.yes, (dialog, button) -> {
                        Intent i = new Intent(this, MainActivity.class);
                        startActivity(i);
                        this.finish();
                    })
                    .setNegativeButton(R.string.no, (dialog, button) -> {
                        String b = "no";
                    })
                    .show();
        });

        this.group.addOnButtonCheckedListener((currentGroup, checkedId, isChecked) -> {
            if (isChecked) {
                this.toggleRows();
            }
        });

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        AutoCompleteTextView textView = findViewById(R.id.addTripFromInput);
        textView.setThreshold(Constants.AUTOCOMPLETE_TEXT_VIEW_DEFAULT_THRESHOLD);
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (textView.isPerformingCompletion() || charSequence.length() < Constants.AUTOCOMPLETE_TEXT_VIEW_DEFAULT_THRESHOLD) {
                    return;
                }
                executorService.execute(() -> {
                    try {
                        List<Address> addresses = geocoder.getFromLocationName(charSequence.toString(), Constants.AUTOCOMPLETE_TEXT_VIEW_DEFAULT_RESULTS_COUNT);

                        handler.post(() -> {
                            List<String> names = addresses
                                    .stream()
                                    .filter(a -> a.getCountryName() != null && a.getLocality() != null)
                                    .map(a -> String.format(Locale.getDefault(), "%s - %s %6.4f, %6.4f", a.getLocality(), a.getCountryName(), a.getLatitude(), a.getLongitude()))
                                    .collect(Collectors.toList());
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, names);
                            textView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {}
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

        double lat = b.getDouble(Constants.LATITUDE);
        double lon = b.getDouble(Constants.LONGITUDE);
        DecimalFormat f = new DecimalFormat("##.00000");
        String text = f.format(lat) + ", " + f.format(lon);
        EditText input = findViewById(id);
        input.setText(text);
    }

    private void toggleRows() {
        for ( int i = 0; i < this.dayOfWeekPickerRow.getChildCount(); i++ ){
            View child = this.dayOfWeekPickerRow.getChildAt(i);
            child.setEnabled(!child.isEnabled());
        }

        for ( int i = 0; i < this.selectTimeRow1.getChildCount(); i++ ){
            View child = this.selectTimeRow1.getChildAt(i);
            child.setEnabled(!child.isEnabled());
        }
    }
}