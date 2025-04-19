package com.example.firstblock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class SelectTypeFragment extends Fragment {

    private RadioGroup editionRadioGroup;
    private Button nextBtn, backBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_select_type, container, false);

        // Initialize views
        editionRadioGroup = view.findViewById(R.id.editionRadioGroup);
        nextBtn = view.findViewById(R.id.NextBtn);
        backBtn = view.findViewById(R.id.BackBtn);

        // Next button logic
        nextBtn.setOnClickListener(v -> {
            int selectedId = editionRadioGroup.getCheckedRadioButtonId();

            if (selectedId == -1) {
                Toast.makeText(getContext(), "Please select an edition", Toast.LENGTH_SHORT).show();
                return;
            }

            // Determine the selected edition type
            String selectedEdition = "";
            if (selectedId == R.id.JavaEditionBtn) {
                selectedEdition = "Java"; // "Java" edition selected
            } else if (selectedId == R.id.BedrockEditionBtn) {
                selectedEdition = "Bedrock"; // "Bedrock" edition selected
            }

            // Pass the selected edition type to the next fragment
            Fragment nextFragment;
            if ("Java".equals(selectedEdition)) {
                nextFragment = new SelectJavaVersionFragment(); // Java fragment
            } else {
                nextFragment = new SelectBedrockVersionFragment(); // Bedrock fragment
            }

            // Create a bundle to pass the selected edition type
            Bundle bundle = new Bundle();
            bundle.putString("selected_edition", selectedEdition); // Add the edition type to the bundle
            nextFragment.setArguments(bundle); // Set the bundle in the next fragment

            // Start the next fragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, nextFragment); // container ID from activity_main.xml
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // Back button logic
        backBtn.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
}
