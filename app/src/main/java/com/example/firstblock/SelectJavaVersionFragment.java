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
    private String selectedEdition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_java_version, container, false);

        javaVersionGroup = view.findViewById(R.id.JavaVersionGroup);
        nextBtn = view.findViewById(R.id.NextBtn);
        backBtn = view.findViewById(R.id.BackBtn);

        Bundle previousBundle = getArguments();
        if (previousBundle != null) {
            selectedEdition = previousBundle.getString("selected_edition", "N/A");
        } else {
            selectedEdition = "N/A";
        }

        nextBtn.setOnClickListener(v -> {
            int selectedId = javaVersionGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                selectedVersion = selectedRadioButton.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("selected_version", selectedVersion);
                bundle.putString("selected_edition", selectedEdition);

                Fragment nextFragment = new SelectJavaLoaderFragment();
                nextFragment.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, nextFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(getContext(), "Please select a Java version", Toast.LENGTH_SHORT).show();
            }
        });

        backBtn.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            } else {
                requireActivity().onBackPressed();
            }
        });

        return view;
    }
}
