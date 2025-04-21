package com.example.firstblock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class PlayersPanel extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.players_panel, container, false);

        // Back button functionality
        ImageButton backBtn = view.findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Find the PlayerList, EditPlayers layout, and PlayersTxtView
        LinearLayout playerList = view.findViewById(R.id.PlayerList);
        LinearLayout editPlayers = view.findViewById(R.id.EditPlayers);
        TextView playersTxtView = view.findViewById(R.id.PlayersTxtView);

        // Initially hide EditPlayers
        editPlayers.setVisibility(View.GONE);

        // Set up the default radio button (AllFilter) to be selected
        RadioGroup filtersRadioGrp = view.findViewById(R.id.FiltersRadioGrp);
        RadioButton allFilter = view.findViewById(R.id.AllFilter);
        allFilter.setChecked(true);  // Make sure AllFilter is the default selected

        // Update PlayersTxtView text based on the selected filter
        filtersRadioGrp.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.AllFilter) {
                playersTxtView.setText("All Players");
            } else if (checkedId == R.id.WhitelistFilter) {
                playersTxtView.setText("Whitelist Players");
            } else if (checkedId == R.id.BannedFilter) {
                playersTxtView.setText("Banned Players");
            }
        });

        // Set up a listener for checkbox changes
        for (int i = 0; i < playerList.getChildCount(); i++) {
            LinearLayout playerDetails = (LinearLayout) playerList.getChildAt(i);
            CheckBox selectCheckBox = playerDetails.findViewById(R.id.SelectCheckBox);

            // Make the PlayerDetails clickable to toggle the checkbox
            playerDetails.setOnClickListener(v -> {
                selectCheckBox.setChecked(!selectCheckBox.isChecked()); // Toggle the checkbox state
            });

            selectCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                boolean isAnySelected = false;
                // Check if any checkbox is selected
                for (int j = 0; j < playerList.getChildCount(); j++) {
                    LinearLayout player = (LinearLayout) playerList.getChildAt(j);
                    CheckBox cb = player.findViewById(R.id.SelectCheckBox);
                    if (cb.isChecked()) {
                        isAnySelected = true;
                        break;
                    }
                }
                // Show or hide the EditPlayers layout based on selection
                editPlayers.setVisibility(isAnySelected ? View.VISIBLE : View.GONE);
            });
        }

        return view;
    }
}