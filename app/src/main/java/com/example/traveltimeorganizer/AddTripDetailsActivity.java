package com.example.traveltimeorganizer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.traveltimeorganizer.data.TripManager;
import com.example.traveltimeorganizer.data.models.Trip;
import com.example.traveltimeorganizer.utils.Constants;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.timepicker.TimeFormat;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.utils.BonusPackHelper;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class AddTripDetailsActivity extends AppCompatActivity {
    private Date tripDateTime;
    private Trip trip;
    private EditText addTripDateInput;
    private EditText addTripMinEarlierInput;
    private AutoCompleteTextView addTripFromInput;
    private AutoCompleteTextView addTripToInput;
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

        this.initDayOfWeekSelector();

        this.addTripDateInput = findViewById(R.id.addTripDate);
        this.trip = new Trip();
        this.trip.setRepeatOnDay(0);
        this.addTripMinEarlierInput = findViewById(R.id.addTripMinEarlier);
        this.group = findViewById(R.id.toggleButtonGroup);
        this.dayOfWeekPickerRow = findViewById(R.id.dayOfWeekPickerRow);
        this.selectTimeRow1 = findViewById(R.id.selectTimeRow1);
        this.addTripFromInput = findViewById(R.id.addTripFromInput);
        this.addTripToInput = findViewById(R.id.addTripToInput);
        for (int i = 0; i < this.selectTimeRow1.getChildCount(); i++) {
            this.selectTimeRow1.getChildAt(i).setEnabled(false);
        }
        this.setListeners();
        this.registerActivityResults();

        this.geocoder = new GeocoderNominatim(Locale.getDefault(), getText(R.string.app_name).toString());
    }

    private void initDayOfWeekSelector() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        for (Map.Entry<Integer, String> entry : Constants.DAY_OF_WEEK_INDEX_MAP.entrySet()) {
            DayOfWeekButtonFragment fragment = DayOfWeekButtonFragment.newInstance(entry.getKey(), entry.getValue());
            transaction.add(R.id.calendarButtonGroup, fragment);
        }
        transaction.commit();
    }


    private @NonNull DatePickerDialog getDatePickerDialog() {
        Calendar now = Calendar.getInstance();

        return new DatePickerDialog(this, (datePicker, year, month, day) -> {
            TimePickerDialog timePickerDialog = getTimePickerDialogForDatePicker(now, day, month, year);
            timePickerDialog.show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
    }

    private @NonNull TimePickerDialog getTimePickerDialogForDatePicker(Calendar now, int day, int month, int year) {
        return new TimePickerDialog(this, (timePicker, hour, minute) -> {
            Calendar chosenDate = Calendar.getInstance();
            chosenDate.set(year, month, day, hour, minute);
            this.tripDateTime = chosenDate.getTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            String fullDateTime = formatter.format(LocalDateTime.of(year, month, day, hour, minute));

            this.addTripDateInput.setText(fullDateTime);
            this.trip.setExecuteOn(fullDateTime);
        }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
    }

    private void setListeners() {
        this.addTripDateInput.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = getDatePickerDialog();
            datePickerDialog.show();
        });

        SwitchCompat addTripOnTimeSwitch = findViewById(R.id.addTripOnTimeSwitch);
        addTripOnTimeSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                trip.setMinEarlier(0);
            }
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
//            new AlertDialog.Builder(this)
//                    .setMessage(R.string.save_trip_popup_text)
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton(R.string.yes, (dialog, button) -> {
//                        Intent i = new Intent(this, MainActivity.class);
//                        startActivity(i);
//                        this.finish();
//                    })
//                    .setNegativeButton(R.string.no, (dialog, button) -> {
//                        String b = "no";
//                    })
////                    .show();

            boolean hasSetPlaceFrom = this.setPlaceInfo(this.addTripFromInput);
            boolean hasSetPlaceTo = this.setPlaceInfo(this.addTripToInput);

            if (!hasSetPlaceFrom || !hasSetPlaceTo) {
                Toast.makeText(this, "Грешен формат на данните", Toast.LENGTH_LONG).show();
                return;
            }
            TripManager manager = new TripManager(this);
            ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
            waypoints.add(new GeoPoint(trip.getFromLatitude(), trip.getFromLongitude()));
            waypoints.add(new GeoPoint(trip.getToLatitude(), trip.getToLongitude()));

            RoadManager roadManager = new OSRMRoadManager(this, BonusPackHelper.DEFAULT_USER_AGENT);
            Road road = roadManager.getRoad(waypoints);
            this.trip.setTripTime(road.mDuration);
            this.trip.setActive(true);


            manager.addTrip(this.trip);

            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            this.finish();
        });

        this.group.addOnButtonCheckedListener((currentGroup, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.buttonPeriodically) {
                    this.trip.setExecuteOn(null);
                }

                else if (checkedId == R.id.buttonOneTime) {
                    this.trip.setRepeatOnDay(null);
                    this.trip.setRepeatOnTime(null);
                }

                this.toggleRows();
                this.addTripDateInput.setText("");
            }
        });

        MaterialButtonToggleGroup calendarButtonGroup = findViewById(R.id.calendarButtonGroup);
        calendarButtonGroup.addOnButtonCheckedListener((currentGroup, checkedId, isChecked) -> {
            @Nullable Integer repeatOnDayValue = this.trip.getRepeatOnDay();
            if (repeatOnDayValue == null) {
                repeatOnDayValue = 0;
            }

            if (isChecked) {
                this.trip.setRepeatOnDay(repeatOnDayValue + checkedId);
            }
            else {
                this.trip.setRepeatOnDay(repeatOnDayValue - checkedId);
            }
        });

        this.addTripFromInput.setThreshold(Constants.AUTOCOMPLETE_TEXT_VIEW_DEFAULT_THRESHOLD);
        this.addTripFromInput.addTextChangedListener(this.getWatcher(this.addTripFromInput));

        AutoCompleteTextView addTripToInput = findViewById(R.id.addTripToInput);
        addTripToInput.setThreshold(Constants.AUTOCOMPLETE_TEXT_VIEW_DEFAULT_THRESHOLD);
        addTripToInput.addTextChangedListener(this.getWatcher(addTripToInput));

        this.addTripMinEarlierInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                trip.setMinEarlier(Integer.parseInt(editable.toString()));
            }
        });

        findViewById(R.id.clockBtn).setOnClickListener(view -> {
            Calendar now = Calendar.getInstance();
            TimePickerDialog dialog = new TimePickerDialog(this, (timePicker, hour, minute) -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String time = formatter.format(LocalTime.of(hour, minute));
                this.addTripDateInput.setText(time);
                this.trip.setRepeatOnTime(time);
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
            dialog.show();
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
        String text = b.getString(Constants.TEXT);

        AutoCompleteTextView textView = findViewById(id);
        textView.setText(text);


    }

    private void toggleRows() {
        for (int i = 0; i < this.dayOfWeekPickerRow.getChildCount(); i++) {
            View child = this.dayOfWeekPickerRow.getChildAt(i);
            child.setEnabled(!child.isEnabled());
        }

        for (int i = 0; i < this.selectTimeRow1.getChildCount(); i++) {
            View child = this.selectTimeRow1.getChildAt(i);
            child.setEnabled(!child.isEnabled());
        }
    }

    private TextWatcher getWatcher(AutoCompleteTextView textView) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

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
                                    .map(a -> String.format(Locale.forLanguageTag("en"), Constants.PLACE_INPUT_STRING_FORMAT, a.getLocality(), a.getCountryName(), a.getLatitude(), a.getLongitude()))
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
            public void afterTextChanged(Editable editable) {
            }
        };
    }

    private boolean setPlaceInfo(AutoCompleteTextView textView) {
        String[] splitted = textView.getText().toString().split("-");
        String place = null;
        String[] coordinates = splitted[0].split(", ");
        if (splitted.length > 1) {
            place = splitted[0].trim();
            splitted = textView.getText().toString().split(": ");
            coordinates = splitted.length > 1 ? splitted[1].split(", ") : splitted;
        }

        double lat = 0;
        double lon = 0;
        if (coordinates.length > 1) {
            try {
                lat = Double.parseDouble(coordinates[0]);
                lon = Double.parseDouble(coordinates[1]);
            }
            catch (NumberFormatException e) {
                return false;
            }
        }
        else {
            return false;
        }

        if (textView.getId() == R.id.addTripFromInput) {
            trip.setFromPlace(place);
            trip.setFromLatitude(lat);
            trip.setFromLongitude(lon);
        }
        else {
            trip.setToPlace(place);
            trip.setToLatitude(lat);
            trip.setToLongitude(lon);
        }

        return true;
    }
}