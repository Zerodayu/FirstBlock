package com.example.firstblock;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class InfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // Find the themeInfo TextView and set a click listener
        TextView themeInfo = view.findViewById(R.id.themeInfo);
        themeInfo.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Using System Default", Toast.LENGTH_SHORT).show();
        });

        // Find the version TextView and set a click listener to show the app version
        TextView versionTextView = view.findViewById(R.id.versionView); // Assuming you have a TextView with this ID
        versionTextView.setOnClickListener(v -> {
            // Get app version and show it in a Toast
            String version = getAppVersion();
            Toast.makeText(requireContext(), "Version " + version, Toast.LENGTH_SHORT).show();
        });

        // Find the StorageDataView TextView and show the app file location
        TextView storageDataView = view.findViewById(R.id.StorageDataView); // Assuming you have a TextView with this ID
        storageDataView.setOnClickListener(v -> {
            String appLocation = getAppFileLocation();
            Toast.makeText(requireContext(), "App File Location: " + appLocation, Toast.LENGTH_LONG).show();
        });

        return view;
    }

    // Method to get the app version
    private String getAppVersion() {
        try {
            // Get the current app's version
            PackageInfo packageInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "N/A"; // Return "N/A" in case version retrieval fails
        }
    }

    // Method to get the app file location
    private String getAppFileLocation() {
        // Get the path to the app's data directory
        String appDataPath = requireContext().getFilesDir().getAbsolutePath();

        // Optionally, you can also show the external storage path:
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();

        // Returning the app data directory path
        return "Internal Storage: " + appDataPath + "\nExternal Storage: " + externalStorage;
    }
}
