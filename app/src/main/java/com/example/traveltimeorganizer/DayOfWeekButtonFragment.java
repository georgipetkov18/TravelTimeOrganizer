package com.example.traveltimeorganizer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import org.w3c.dom.Text;

import java.util.function.Consumer;

public class DayOfWeekButtonFragment extends Fragment {
    private static final String ID = "id";
    private static final String TEXT = "text";

    private int id;
    private String text;

    public DayOfWeekButtonFragment() {
        // Required empty public constructor
    }

    public static DayOfWeekButtonFragment newInstance(int id, String text) {
        DayOfWeekButtonFragment fragment = new DayOfWeekButtonFragment();
        Bundle args = new Bundle();
        args.putInt(ID, id);
        args.putString(TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ID);
            text = getArguments().getString(TEXT);
        }
        else {
            text = "";
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_day_of_week_button, container, false);

        MaterialButton button = view.findViewById(R.id.dayOfWeekButtonFragment);
        button.setText(this.text);
        button.setTag(id);
        button.setId(id);
        return view;
    }
}