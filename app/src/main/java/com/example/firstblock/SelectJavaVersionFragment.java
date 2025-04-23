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

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class SelectJavaVersionFragment extends Fragment {

    private RadioGroup javaVersionGroup;
    private RadioButton javaVersionBtnTemplate;
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

        // Get edition passed from previous fragment
        Bundle previousBundle = getArguments();
        selectedEdition = (previousBundle != null)
                ? previousBundle.getString("selected_edition", "N/A")
                : "N/A";

        loadJavaVersions(inflater);

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

    private void loadJavaVersions(LayoutInflater inflater) {
        try {
            InputStream is = requireContext().getAssets().open("java_server_links.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);

            javaVersionGroup.removeView(javaVersionBtnTemplate); // Remove the template

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String version = keys.next();

                RadioButton newRadioButton = (RadioButton) inflater.inflate(R.layout.item_radio_button, javaVersionGroup, false);
                newRadioButton.setText(version);
                newRadioButton.setId(View.generateViewId());

                javaVersionGroup.addView(newRadioButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
