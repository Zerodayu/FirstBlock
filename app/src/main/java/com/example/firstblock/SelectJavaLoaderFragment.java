package com.example.firstblock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;

public class SelectJavaLoaderFragment extends Fragment {

    private RadioGroup loaderRadioGroup;
    private Button nextBtn;
    private Button backBtn;  // Declare back button
    private String selectedLoader;
    private String selectedVersion;  // Declare as instance variable
    private String selectedEdition;  // Declare as instance variable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_java_loader, container, false);

        // Initialize views
        loaderRadioGroup = view.findViewById(R.id.JavaVersionGroup);
        nextBtn = view.findViewById(R.id.NextBtn);
        backBtn = view.findViewById(R.id.BackBtn);  // Initialize back button

        // Retrieve the selected Java version and edition from the previous fragments
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedVersion = bundle.getString("selected_version", "N/A");  // Set selectedVersion
            selectedEdition = bundle.getString("selected_edition", "N/A");  // Set selectedEdition

            // Show the selected version and edition
            Toast.makeText(getContext(), "Minecraft: " + selectedVersion + " " + selectedEdition, Toast.LENGTH_SHORT).show();
        }

        // Handle the "Next" button click
        nextBtn.setOnClickListener(v -> {
            int selectedId = loaderRadioGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                // Get the selected loader
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                selectedLoader = selectedRadioButton.getText().toString();

                // Pass the selected loader along with the previously passed edition and version
                Bundle newBundle = new Bundle();
                newBundle.putString("selected_loader", selectedLoader);
                newBundle.putString("selected_edition", selectedEdition);  // Pass selectedEdition
                newBundle.putString("selected_version", selectedVersion);  // Pass selectedVersion

                // Create and load the next fragment (ConfirmCreateFragment)
                Fragment nextFragment = new ConfirmCreateFragment();
                nextFragment.setArguments(newBundle);

                // Perform the fragment transaction
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, nextFragment);
                transaction.addToBackStack(null);  // Add to back stack for proper navigation
                transaction.commit();
            } else {
                // Show a message if no loader is selected
                Toast.makeText(getContext(), "Please select a loader", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle the "Back" button click
        backBtn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                // Go back to the previous fragment if the back stack has entries
                fragmentManager.popBackStack();
            } else {
                // If no fragments are in the back stack, exit the activity or handle it in a different way
                getActivity().onBackPressed();  // This will go back to the previous activity or exit the app
            }
        });

        return view;
    }
}