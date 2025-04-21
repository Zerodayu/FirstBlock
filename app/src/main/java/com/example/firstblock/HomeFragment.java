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
    private ProgressBar globalProgressBar;
    private TextView onlineServerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        createBtn = view.findViewById(R.id.CreateBtn);
        globalProgressBar = view.findViewById(R.id.serverStartBar);
        onlineServerView = view.findViewById(R.id.onlineServerView);
        LinearLayout serverListContainer = view.findViewById(R.id.ServerList);

        // Hide the online server view by default
        onlineServerView.setVisibility(View.GONE);

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
            View lineView = serverView.findViewById(R.id.line);

            nameText.setText(server.getName());
            detailsText.setText(server.getEdition() + " | " + server.getVersion() + " | " + server.getLoader());

            allCtrlBtns.add(ctrlBtn);

            final boolean[] isRunning = {false};

            ctrlBtn.setOnClickListener(v -> {
                if (isRunning[0]) {
                    // Stop server
                    ctrlBtn.clearAnimation();
                    ctrlBtn.setImageResource(R.drawable.play_icon);
                    isRunning[0] = false;
                    globalProgressBar.setProgress(0);

                    Toast.makeText(getContext(), "Server Stopped", Toast.LENGTH_SHORT).show();

                    // Hide the online server view
                    onlineServerView.setText("No Online Server");
                    onlineServerView.setVisibility(View.GONE);

                    // Re-enable all buttons
                    for (ImageButton btn : allCtrlBtns) {
                        btn.setEnabled(true);
                        btn.setAlpha(1.0f);
                    }

                    lineView.setAlpha(0.2f);

                } else {
                    Toast.makeText(getContext(), "Server Starting", Toast.LENGTH_SHORT).show();

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

                    final int[] progress = {0};
                    globalProgressBar.setMax(100);
                    globalProgressBar.setProgress(progress[0]);

                    final Runnable updateProgress = new Runnable() {
                        @Override
                        public void run() {
                            if (progress[0] < 100) {
                                progress[0] += 5;
                                globalProgressBar.setProgress(progress[0]);
                                ctrlBtn.postDelayed(this, 200);
                            } else {
                                ctrlBtn.clearAnimation();
                                ctrlBtn.setImageResource(R.drawable.stop_icon);
                                isRunning[0] = true;

                                Toast.makeText(getContext(), "Server Running", Toast.LENGTH_SHORT).show();

                                // Show the online server view
                                onlineServerView.setVisibility(View.VISIBLE);
                                onlineServerView.setText(server.getName() + " " + server.getVersion() + " " + server.getLoader());

                                ctrlBtn.setEnabled(true);
                                lineView.setAlpha(1.0f);
                            }
                        }
                    };

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


