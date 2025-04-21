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
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        // Use TextView instead of Button now
        TextView themeInfo = view.findViewById(R.id.themeInfo);
        themeInfo.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Using System Default", Toast.LENGTH_SHORT).show();
        });

        TextView versionView = view.findViewById(R.id.versionView);
        versionView.setOnClickListener(v -> {
            String version = getAppVersion();
            Toast.makeText(requireContext(), "Version " + version, Toast.LENGTH_SHORT).show();
        });

        TextView storageDataView = view.findViewById(R.id.StorageDataView);
        storageDataView.setOnClickListener(v -> {
            String appLocation = getAppFileLocation();
            Toast.makeText(requireContext(), "App File Location: " + appLocation, Toast.LENGTH_LONG).show();
        });

        return view;
    }

    private String getAppVersion() {
        try {
            PackageInfo packageInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    private String getAppFileLocation() {
        String appDataPath = requireContext().getFilesDir().getAbsolutePath();
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        return "Internal Storage: " + appDataPath + "\nExternal Storage: " + externalStorage;
    }
}
