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

public class SelectJavaVersionFragment extends Fragment {

    private RadioGroup javaVersionGroup;
    private Button nextBtn;
    private Button backBtn;
    private String selectedVersion;
    private String selectedEdition;  // Declare this as an instance variable

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_java_version, container, false);

        // Initialize views
        javaVersionGroup = view.findViewById(R.id.JavaVersionGroup);
        nextBtn = view.findViewById(R.id.NextBtn);
        backBtn = view.findViewById(R.id.BackBtn);

        // Retrieve the arguments passed from the previous fragment
        Bundle previousBundle = getArguments();
        if (previousBundle != null) {
            selectedEdition = previousBundle.getString("selected_edition", "N/A"); // Now it's set here
        } else {
            selectedEdition = "N/A"; // Default value
        }

        // Next button logic
        nextBtn.setOnClickListener(v -> {
            int selectedId = javaVersionGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                // Get the selected Java version
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                selectedVersion = selectedRadioButton.getText().toString();

                // Prepare the bundle with the selected version and edition
                Bundle bundle = new Bundle();
                bundle.putString("selected_version", selectedVersion);
                bundle.putString("selected_edition", selectedEdition);

                // Create the next fragment
                Fragment nextFragment = new SelectJavaLoaderFragment();
                nextFragment.setArguments(bundle);

                // Perform the fragment transaction
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, nextFragment);
                transaction.addToBackStack(null);  // Add the transaction to the back stack
                transaction.commit();
            } else {
                // Show a message if no Java version is selected
                Toast.makeText(getContext(), "Please select a Java version", Toast.LENGTH_SHORT).show();
            }
        });

        // Back button logic
        backBtn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                // Go back to the previous fragment if the back stack has entries
                fragmentManager.popBackStack();
            } else {
                // If no fragments are in the back stack, you can choose to do something else
                // For example, you could finish the activity or handle it in another way
                getActivity().onBackPressed();
            }
        });

        return view;
    }
}
