package com.example.firstblock;

import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

public class DeviceFragment extends Fragment {

    private TextView deviceNameTextView, processorTextView, storageTextView, networkStatusTextView, ramTextView;
    private ProgressBar ramProgressBar;

    private final Handler handler = new Handler();
    private final int updateInterval = 3000; // Update every 3 seconds

    private final Runnable updateRunnable = this::updateDeviceInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        // Find the TextViews
        deviceNameTextView = view.findViewById(R.id.srcName);
        processorTextView = view.findViewById(R.id.srcProc);
        storageTextView = view.findViewById(R.id.srcStorage);
        networkStatusTextView = view.findViewById(R.id.srcNetwork);
        ramTextView = view.findViewById(R.id.srcRam);
        ramProgressBar = view.findViewById(R.id.RamProgressBar);

        // Set static values
        String deviceName = Build.DEVICE;
        String processor = Build.HARDWARE;
        deviceNameTextView.setText(deviceName);
        processorTextView.setText(processor);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(updateRunnable); // Start periodic updates
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateRunnable); // Stop updates
    }

    private void updateDeviceInfo() {
        if (getContext() == null) return;

        // Update storage
        long appSizeBytes = getDirSize(requireContext().getFilesDir());
        String formattedSize = formatSize(appSizeBytes);
        storageTextView.setText(formattedSize);

        // Update network status
        String networkStatus = getNetworkStatus(requireContext());
        networkStatusTextView.setText(networkStatus);

        // Update RAM usage text
        String ramInfo = getRAMInfo(requireContext());
        ramTextView.setText(ramInfo);

        // Update RAM progress bar
        int ramUsagePercent = getRAMUsagePercent(requireContext());
        ramProgressBar.setProgress(ramUsagePercent);

        // Schedule next update
        handler.postDelayed(updateRunnable, updateInterval);
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

    private String getRAMInfo(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // Get app memory usage
        int pid = android.os.Process.myPid();
        android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(new int[]{pid});
        int appRamKb = memoryInfoArray[0].getTotalPss();
        long appRamBytes = appRamKb * 1024L;

        // Get total RAM
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalRamBytes = memoryInfo.totalMem;

        return formatSize(appRamBytes) + " / " + formatSize(totalRamBytes);
    }

    private int getRAMUsagePercent(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        int pid = android.os.Process.myPid();
        android.os.Debug.MemoryInfo[] memoryInfoArray = activityManager.getProcessMemoryInfo(new int[]{pid});
        int appRamKb = memoryInfoArray[0].getTotalPss();
        long appRamBytes = appRamKb * 1024L;

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalRamBytes = memoryInfo.totalMem;

        if (totalRamBytes == 0) return 0;
        return (int) ((appRamBytes * 100) / totalRamBytes);
    }
}
