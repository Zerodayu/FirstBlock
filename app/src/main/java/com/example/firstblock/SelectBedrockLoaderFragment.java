package com.example.firstblock;

import android.os.Bundle;
import android.util.Log;
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

public class SelectBedrockLoaderFragment extends Fragment {

    private RadioGroup loaderRadioGroup;
    private Button nextBtn;
    private Button backBtn;
    private String selectedLoader;
    private String selectedVersion;
    private String selectedEdition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_bedrock_loader, container, false);

        loaderRadioGroup = view.findViewById(R.id.BedrockLoaderGroup);
        nextBtn = view.findViewById(R.id.NextBtn);
        backBtn = view.findViewById(R.id.BackBtn);

        Bundle previousBundle = getArguments();
        if (previousBundle != null) {
            selectedEdition = previousBundle.getString("selected_edition", "N/A");
            selectedVersion = previousBundle.getString("selected_version", "N/A");

            Toast.makeText(getContext(), "Minecraft: " + selectedVersion + " " + selectedEdition, Toast.LENGTH_SHORT).show();
        }

        // Load the Bedrock loaders from the JSON file
        loadBedrockLoaders(inflater);

        nextBtn.setOnClickListener(v -> {
            int selectedId = loaderRadioGroup.getCheckedRadioButtonId();

            if (selectedId != -1) {
                RadioButton selectedRadioButton = view.findViewById(selectedId);
                selectedLoader = selectedRadioButton.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("selected_loader", selectedLoader);
                bundle.putString("selected_version", selectedVersion);
                bundle.putString("selected_edition", selectedEdition);

                Fragment nextFragment = new ConfirmCreateFragment();
                nextFragment.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, nextFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            } else {
                Toast.makeText(getContext(), "Please select a loader", Toast.LENGTH_SHORT).show();
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

    private void loadBedrockLoaders(LayoutInflater inflater) {
        try {
            InputStream is = getContext().getAssets().open("bedrock_server_links.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, StandardCharsets.UTF_8);
            JSONObject jsonObject = new JSONObject(json);

            // Get the loaders for the selected version
            JSONObject versionData = jsonObject.optJSONObject(selectedVersion);
            if (versionData != null) {
                Iterator<String> keys = versionData.keys();
                while (keys.hasNext()) {
                    String loader = keys.next();

                    // Log the loader to confirm data loading
                    Log.d("Loaders", "Found loader: " + loader);

                    // Create a RadioButton for each loader
                    RadioButton newRadioButton = (RadioButton) inflater.inflate(R.layout.item_radio_button, loaderRadioGroup, false);
                    newRadioButton.setText(loader);
                    newRadioButton.setId(View.generateViewId());

                    // Add the RadioButton to the RadioGroup
                    loaderRadioGroup.addView(newRadioButton);
                }
            } else {
                Toast.makeText(getContext(), "No loaders available for this version.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to load Bedrock loaders.", Toast.LENGTH_SHORT).show();
        }
    }
}
