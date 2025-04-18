package com.example.firstblock;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

public class DeviceFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        // Find the TextViews
        TextView deviceNameTextView = view.findViewById(R.id.srcName);
        TextView processorTextView = view.findViewById(R.id.srcProc);
        TextView storageTextView = view.findViewById(R.id.srcStorage);
        TextView networkStatusTextView = view.findViewById(R.id.srcNetwork); // New TextView

        // Get device info
        String deviceName = Build.DEVICE;
        String processor = Build.HARDWARE;

        // Set values
        deviceNameTextView.setText(deviceName);
        processorTextView.setText(processor);

        // Calculate and display app storage usage
        long appSizeBytes = getDirSize(requireContext().getFilesDir());
        String formattedSize = formatSize(appSizeBytes);
        storageTextView.setText(formattedSize);

        // Get and display network status
        String networkStatus = getNetworkStatus(requireContext());
        networkStatusTextView.setText(networkStatus);

        return view;
    }

    private long getDirSize(File dir) {
        long size = 0;
        if (dir != null && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        size += file.length();
                    } else {
                        size += getDirSize(file);
                    }
                }
            }
        }
        return size;
    }

    private String formatSize(long sizeInBytes) {
        double size = sizeInBytes;
        if (size >= 1_073_741_824) {
            return String.format("%.2f GB", size / 1_073_741_824);
        } else if (size >= 1_048_576) {
            return String.format("%.2f MB", size / 1_048_576);
        } else {
            return String.format("%.2f KB", size / 1024);
        }
    }

    private String getNetworkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return "Unknown";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    return "Connected (Wi-Fi)";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    return "Connected (Mobile Data)";
                } else {
                    return "Connected (Other)";
                }
            } else {
                return "Offline";
            }
        } else {
            android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    return "Connected (Wi-Fi)";
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return "Connected (Mobile Data)";
                } else {
                    return "Connected (Other)";
                }
            } else {
                return "Offline";
            }
        }
    }
}
