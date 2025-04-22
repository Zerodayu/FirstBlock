package com.example.firstblock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

public class BackupPanel extends Fragment {

    private Button addBtn;
    private Button delBtn;
    private Button loadBtn;
    private LinearLayout backupList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.backup_panel, container, false);

        ImageButton backBtn = view.findViewById(R.id.BackBtn);
        addBtn = view.findViewById(R.id.AddBtn);
        delBtn = view.findViewById(R.id.DelBtn);
        loadBtn = view.findViewById(R.id.LoadBtn);
        backupList = view.findViewById(R.id.BackupList);

        backBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed(); // This simulates pressing the back button
            }
        });

        // Initial state: only AddBtn visible
        showOnlyAddBtn();

        // Loop through child views of backupList and hook into each checkbox
        for (int i = 0; i < backupList.getChildCount(); i++) {
            View backupItem = backupList.getChildAt(i);
            LinearLayout backupDetails = backupItem.findViewById(R.id.BackupDetails);
            CheckBox checkBox = backupItem.findViewById(R.id.SelectCheckBox);

            if (checkBox != null && backupDetails != null) {

                // Handle checkbox toggle when item row is clicked
                backupDetails.setOnClickListener(v -> {
                    checkBox.setChecked(!checkBox.isChecked()); // toggle checkbox
                });

                // Listen for checkbox state changes to update button visibility
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateButtonsVisibility());
            }
        }

        return view;
    }

    // Check if any checkbox is selected
    private boolean isAnyItemSelected() {
        for (int i = 0; i < backupList.getChildCount(); i++) {
            View backupItem = backupList.getChildAt(i);
            CheckBox checkBox = backupItem.findViewById(R.id.SelectCheckBox);
            if (checkBox != null && checkBox.isChecked()) {
                return true;
            }
        }
        return false;
    }

    // Update visibility of buttons
    private void updateButtonsVisibility() {
        if (isAnyItemSelected()) {
            addBtn.setVisibility(View.GONE);
            delBtn.setVisibility(View.VISIBLE);
            loadBtn.setVisibility(View.VISIBLE);
        } else {
            showOnlyAddBtn();
        }
    }

    private void showOnlyAddBtn() {
        addBtn.setVisibility(View.VISIBLE);
        delBtn.setVisibility(View.GONE);
        loadBtn.setVisibility(View.GONE);
    }
}