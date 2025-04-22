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

public class ModsPanel extends Fragment {

    private Button addBtn;
    private Button delBtn;
    private LinearLayout modListLayout; // This will contain the list of mods

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.mods_panel, container, false);

        ImageButton backBtn = view.findViewById(R.id.BackBtn);
        addBtn = view.findViewById(R.id.AddBtn);
        delBtn = view.findViewById(R.id.DelBtn);
        modListLayout = view.findViewById(R.id.ModList);

        // Set up the back button
        backBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().onBackPressed(); // This simulates pressing the back button
            }
        });

        // Add logic to update button visibility based on selection state
        updateButtonsVisibility();

        // Iterate through the mod list and set listeners for ModDetails click
        for (int i = 0; i < modListLayout.getChildCount(); i++) {
            View modItem = modListLayout.getChildAt(i);
            LinearLayout modDetails = modItem.findViewById(R.id.ModDetails); // Find the ModDetails layout
            CheckBox modCheckBox = modItem.findViewById(R.id.SelectCheckBox);

            if (modCheckBox != null && modDetails != null) {
                // Make the ModDetails layout clickable to toggle the checkbox
                modDetails.setOnClickListener(v -> {
                    modCheckBox.setChecked(!modCheckBox.isChecked()); // Toggle checkbox state
                });

                // Listener for checkbox state change
                modCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> updateButtonsVisibility());
            }
        }

        return view;
    }

    private void updateButtonsVisibility() {
        boolean anyChecked = false;

        // Check all CheckBoxes in the mod list layout
        for (int i = 0; i < modListLayout.getChildCount(); i++) {
            View modItem = modListLayout.getChildAt(i);
            CheckBox checkBox = modItem.findViewById(R.id.SelectCheckBox);
            if (checkBox != null && checkBox.isChecked()) {
                anyChecked = true;
                break;
            }
        }

        // Show AddBtn if no checkboxes are checked, and DelBtn if at least one is checked
        addBtn.setVisibility(anyChecked ? View.GONE : View.VISIBLE);
        delBtn.setVisibility(anyChecked ? View.VISIBLE : View.GONE);
    }
}
