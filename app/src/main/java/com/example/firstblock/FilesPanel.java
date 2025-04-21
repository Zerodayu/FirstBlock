package com.example.firstblock;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class FilesPanel extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.files_panel, container, false);

        // Find the ImageButton (BackBtn) in the layout
        ImageButton backBtn = view.findViewById(R.id.BackBtn);

        // Set a click listener to handle back navigation
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back navigation, either by popping the fragment or using onBackPressed
                if (getActivity() != null) {
                    getActivity().onBackPressed(); // This simulates pressing the back button
                }
            }
        });

        return view;
    }
}