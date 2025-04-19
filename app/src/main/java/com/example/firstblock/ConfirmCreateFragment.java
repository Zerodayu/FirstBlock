package com.example.firstblock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class ConfirmCreateFragment extends Fragment {

    private TextView typeView, versionView, loaderView;
    private EditText serverNameInput;
    private Button nextBtn, backBtn;

    // Declare instance variables for selected values
    private String selectedEdition, selectedVersion, selectedLoader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_create, container, false);

        // Initialize views
        typeView = view.findViewById(R.id.TypeView);
        versionView = view.findViewById(R.id.VersionView);
        loaderView = view.findViewById(R.id.LoaderView);
        serverNameInput = view.findViewById(R.id.serverNameInput); // Server name input (EditText)
        nextBtn = view.findViewById(R.id.NextBtn);
        backBtn = view.findViewById(R.id.BackBtn);

        // Retrieve the passed data from the arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedEdition = bundle.getString("selected_edition");
            selectedVersion = bundle.getString("selected_version");
            selectedLoader = bundle.getString("selected_loader");

            // Display the selected values
            typeView.setText(selectedEdition);
            versionView.setText(selectedVersion);
            loaderView.setText(selectedLoader);
        }

        // Handle the "Next" button click (for final confirmation or server creation)
        nextBtn.setOnClickListener(v -> {
            // Get the server name input (EditText)
            String serverName = serverNameInput.getText().toString().trim();

            // Check if the server name is empty
            if (serverName.isEmpty()) {
                // Show a toast message if the input is empty
                Toast.makeText(getContext(), "Please enter a server name", Toast.LENGTH_SHORT).show();
            } else if (serverName.length() > 10) {
                // Check if the server name exceeds 10 characters
                Toast.makeText(getContext(), "Server name cannot exceed 10 characters", Toast.LENGTH_SHORT).show();
            } else if (serverName.contains(" ")) {
                // Check if the server name contains spaces
                Toast.makeText(getContext(), "Server name cannot contain spaces", Toast.LENGTH_SHORT).show();
            } else {
                // Proceed with server creation (or next steps)
                Toast.makeText(getContext(), "Creating the: " + serverName + " Server", Toast.LENGTH_SHORT).show();

                // Pass server name and other details to the CreateLoadingFragment
                Bundle newBundle = new Bundle();
                newBundle.putString("server_name", serverName);
                newBundle.putString("selected_edition", selectedEdition);
                newBundle.putString("selected_version", selectedVersion);
                newBundle.putString("selected_loader", selectedLoader);

                // Create and load the CreateLoadingFragment
                Fragment nextFragment = new CreateLoadingFragment();
                nextFragment.setArguments(newBundle);

                // Replace the current fragment with CreateLoadingFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, nextFragment);
                transaction.addToBackStack(null); // Optionally add to the back stack
                transaction.commit();
            }
        });

        // Handle the "Back" button click to return to the previous step
        backBtn.setOnClickListener(v -> {
            requireActivity().onBackPressed(); // Go back to the previous fragment
        });

        return view;
    }
}
