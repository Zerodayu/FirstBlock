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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class SelectBedrockVersionFragment extends Fragment {

    private RadioGroup bedrockVersionGroup;
    private Button nextBtn;
    private Button backBtn;
    private String selectedVersion;
    private String selectedEdition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_bedrock_version, container, false);

        bedrockVersionGroup = view.findViewById(R.id.BedrockVersionGroup);
        nextBtn = view.findViewById(R.id.NextBtn);
        backBtn = view.findViewById(R.id.BackBtn);

        Bundle previousBundle = getArguments();
        if (previousBundle != null) {
            selectedEdition = previousBundle.getString("selected_edition", "N/A");
        } else {
            selectedEdition = "N/A";
        }

        loadBedrockVersions(inflater);

        nextBtn.setOnClickListener(v -> {
            int selectedId = bedrockVersionGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                selectedVersion = selectedRadioButton.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("selected_version", selectedVersion);
                bundle.putString("selected_edition", selectedEdition);

                Fragment nextFragment = new SelectBedrockLoaderFragment();
                nextFragment.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, nextFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(getContext(), "Please select a Bedrock version", Toast.LENGTH_SHORT).show();
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

    private void loadBedrockVersions(LayoutInflater inflater) {
        try {
            InputStream is = getContext().getAssets().open("bedrock_server_links.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);

            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String version = keys.next();

                RadioButton newRadioButton = (RadioButton) inflater.inflate(R.layout.item_radio_button, bedrockVersionGroup, false);
                newRadioButton.setText(version);
                newRadioButton.setId(View.generateViewId());

                bedrockVersionGroup.addView(newRadioButton);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to load Bedrock versions.", Toast.LENGTH_SHORT).show();
        }
    }
}
