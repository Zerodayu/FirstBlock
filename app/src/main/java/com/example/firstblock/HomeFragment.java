package com.example.firstblock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private View createBtn;
    private ProgressBar globalProgressBar; // Reference to the global ProgressBar
    private TextView onlineServerView; // Reference to the TextView displaying the currently running server

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        createBtn = view.findViewById(R.id.CreateBtn);
        globalProgressBar = view.findViewById(R.id.serverStartBar); // Initialize global ProgressBar
        onlineServerView = view.findViewById(R.id.onlineServerView); // Initialize the TextView for online server
        LinearLayout serverListContainer = view.findViewById(R.id.ServerList);

        // Load server data from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("ServerPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("server_list", null);

        List<ServerData> serverDataList = new ArrayList<>();
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ServerData>>() {}.getType();
            serverDataList = gson.fromJson(json, type);
        }

        // Store all ctrl buttons for interaction control
        List<ImageButton> allCtrlBtns = new ArrayList<>();

        // Dynamically add views for each server data
        for (ServerData server : serverDataList) {
            View serverView = inflater.inflate(R.layout.server_data, serverListContainer, false);

            TextView nameText = serverView.findViewById(R.id.ServerName);
            TextView detailsText = serverView.findViewById(R.id.serverDetails);
            ImageButton ctrlBtn = serverView.findViewById(R.id.ServerCtrlBtn);

            nameText.setText(server.getName());
            detailsText.setText(server.getEdition() + " | " + server.getVersion() + " | " + server.getLoader());

            allCtrlBtns.add(ctrlBtn); // Add to button list

            final boolean[] isRunning = {false};

            ctrlBtn.setOnClickListener(v -> {
                if (isRunning[0]) {
                    // Stop server simulation
                    ctrlBtn.clearAnimation();
                    ctrlBtn.setImageResource(R.drawable.play_icon);
                    isRunning[0] = false;
                    globalProgressBar.setProgress(0); // Reset progress bar when stopping

                    // Show toast for server stopped
                    Toast.makeText(getContext(), "Server Stopped", Toast.LENGTH_SHORT).show();

                    // Update the onlineServerView TextView
                    onlineServerView.setText("Online Server: ");

                    // Re-enable all buttons
                    for (ImageButton btn : allCtrlBtns) {
                        btn.setEnabled(true);
                        btn.setAlpha(1.0f);
                    }

                } else {
                    // Show toast for server starting
                    Toast.makeText(getContext(), "Server Starting", Toast.LENGTH_SHORT).show();

                    // Start server simulation
                    ctrlBtn.setImageResource(R.drawable.loading);

                    RotateAnimation rotate = new RotateAnimation(
                            360f, 0f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                            RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    rotate.setDuration(1000);
                    rotate.setRepeatCount(RotateAnimation.INFINITE);
                    rotate.setInterpolator(new LinearInterpolator());

                    ctrlBtn.startAnimation(rotate);
                    ctrlBtn.setEnabled(false);

                    // Disable and fade out other buttons
                    for (ImageButton btn : allCtrlBtns) {
                        if (btn != ctrlBtn) {
                            btn.setEnabled(false);
                            btn.setAlpha(0.3f);
                        }
                    }

                    // Simulate server start with continuous progress bar update
                    final int[] progress = {0}; // Track the progress
                    globalProgressBar.setMax(100); // Set max progress
                    globalProgressBar.setProgress(progress[0]);

                    // Simulate loading with incremental progress
                    final Runnable updateProgress = new Runnable() {
                        @Override
                        public void run() {
                            if (progress[0] < 100) {
                                progress[0] += 5; // Increase progress by 5 each update
                                globalProgressBar.setProgress(progress[0]);

                                // Continue updating until reaching 100%
                                ctrlBtn.postDelayed(this, 200); // Update every 200ms
                            } else {
                                // When progress reaches 100%, stop the animation and change button icon
                                ctrlBtn.clearAnimation();
                                ctrlBtn.setImageResource(R.drawable.stop_icon);
                                isRunning[0] = true;

                                // Show toast for server running
                                Toast.makeText(getContext(), "Server Running", Toast.LENGTH_SHORT).show();

                                // Update the onlineServerView TextView
                                onlineServerView.setText("Online Server: " + server.getName() + " " + server.getVersion() + " " + server.getLoader());

                                ctrlBtn.setEnabled(true);
                            }
                        }
                    };

                    // Start the progress update
                    ctrlBtn.post(updateProgress);
                }
            });

            serverListContainer.addView(serverView);
        }

        // Navigate to create flow
        createBtn.setOnClickListener(v -> {
            Fragment selectTypeFragment = new SelectTypeFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, selectTypeFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
