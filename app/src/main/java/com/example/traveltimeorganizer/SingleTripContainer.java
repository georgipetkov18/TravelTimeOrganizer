package com.example.traveltimeorganizer;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

public class SingleTripContainer extends Fragment {
    private static final String ID = "id";

    private int id;

    public SingleTripContainer() {
        // Required empty public constructor
    }

    public static SingleTripContainer newInstance(int id) {
        SingleTripContainer fragment = new SingleTripContainer();
        Bundle args = new Bundle();
        args.putInt(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_single_trip_container, container, false);
        view.setTag(id);
        view.setId(id);
        return view;
    }
}