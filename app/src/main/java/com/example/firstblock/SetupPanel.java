package com.example.firstblock;

import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class SetupPanel extends Fragment {

    private Switch crackedSwitch, whitelistSwitch, pvpSwitch, flySwitch, monstersSwitch, forceGmodeSwitch, respackReqSwitch;
    private EditText maxPlayersInput, spawnProtInput;
    private Spinner gamemodeSelect, difficultySelect;
    private LinearLayout editBtns;
    private boolean hasChanges = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setup_panel, container, false);

        // Initialize the layout for edit buttons
        editBtns = view.findViewById(R.id.EditBtns);

        // Initialize Switch views
        crackedSwitch = view.findViewById(R.id.CrackedSwitch);
        whitelistSwitch = view.findViewById(R.id.WhitelistSwitch);
        pvpSwitch = view.findViewById(R.id.PvpSwitch);
        flySwitch = view.findViewById(R.id.FlySwitch);
        monstersSwitch = view.findViewById(R.id.MonsterSwitch);
        forceGmodeSwitch = view.findViewById(R.id.ForceGmodeSwitch);
        respackReqSwitch = view.findViewById(R.id.RespackReqSwitch);

        // Initialize EditText views
        maxPlayersInput = view.findViewById(R.id.MaxPlayersInput);
        spawnProtInput = view.findViewById(R.id.SpawnProtInput);

        // Initialize Spinner views
        gamemodeSelect = view.findViewById(R.id.GamemodeSelect);
        difficultySelect = view.findViewById(R.id.DifficultySelect);

        // Populate the Spinners with data
        setUpGamemodeSpinner();
        setUpDifficultySpinner();

        // Set listeners to change trackTint and thumbTint when switched
        setSwitchColor(crackedSwitch);
        setSwitchColor(whitelistSwitch);
        setSwitchColor(pvpSwitch);
        setSwitchColor(flySwitch);
        setSwitchColor(monstersSwitch);
        setSwitchColor(forceGmodeSwitch);
        setSwitchColor(respackReqSwitch);

        // Add text change listeners for EditTexts
        setEditTextListener(maxPlayersInput);
        setEditTextListener(spawnProtInput);

        // Add item selected listeners for Spinners
        setSpinnerListener(gamemodeSelect);
        setSpinnerListener(difficultySelect);

        // Hide the EditBtns initially
        editBtns.setVisibility(View.GONE);

        // Set a click listener to handle back navigation
        ImageButton backBtn = view.findViewById(R.id.BackBtn);
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

    private void setSwitchColor(Switch switchView) {
        // Set default colors
        updateSwitchColor(switchView, switchView.isChecked());

        // Add listener to update colors when switch is toggled
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSwitchColor(switchView, isChecked);

                // Track if any switch was toggled
                hasChanges = true;
                toggleEditBtns();
            }
        });
    }

    private void updateSwitchColor(Switch switchView, boolean isChecked) {
        if (isChecked) {
            // When switched on, set to green
            switchView.setTrackTintList(ContextCompat.getColorStateList(getContext(), R.color.green));
            switchView.setThumbTintList(ContextCompat.getColorStateList(getContext(), R.color.green));
        } else {
            // When switched off, set to default color (system default track color)
            switchView.setTrackTintList(ContextCompat.getColorStateList(getContext(), R.color.gray));
            switchView.setThumbTintList(ContextCompat.getColorStateList(getContext(), R.color.gray));
        }
    }

    // Add listeners for EditText changes
    private void setEditTextListener(EditText editText) {
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // When the EditText loses focus, check if the text has changed
                    hasChanges = true;
                    toggleEditBtns();
                }
            }
        });

        // You can also add TextWatcher if you need real-time detection of text change
        editText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                // Track changes in the text as well
                hasChanges = true;
                toggleEditBtns();
            }
            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }

    // Add listeners for Spinner item selections
    private void setSpinnerListener(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                // Track changes in Spinner selections
                hasChanges = true;
                toggleEditBtns();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // No item selected, but we still handle the case here
            }
        });
    }

    // Method to toggle the visibility of the EditBtns layout
    private void toggleEditBtns() {
        if (hasChanges) {
            editBtns.setVisibility(View.VISIBLE);
        } else {
            editBtns.setVisibility(View.GONE);
        }
    }

    // Set up the gamemode spinner with the options
    private void setUpGamemodeSpinner() {
        ArrayAdapter<CharSequence> gamemodeAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.gamemode_options, R.layout.spinner_item); // Custom spinner layout
        gamemodeAdapter.setDropDownViewResource(R.layout.spinner_item);
        gamemodeSelect.setAdapter(gamemodeAdapter);
    }

    private void setUpDifficultySpinner() {
        ArrayAdapter<CharSequence> difficultyAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.difficulty_options, R.layout.spinner_item); // Custom spinner layout
        difficultyAdapter.setDropDownViewResource(R.layout.spinner_item);
        difficultySelect.setAdapter(difficultyAdapter);
    }
}
