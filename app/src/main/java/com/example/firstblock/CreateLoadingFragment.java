package com.example.firstblock;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CreateLoadingFragment extends Fragment {

    private Button finishBtn;
    private ProgressBar progressBar;
    private TextView nameView, typeView, versionView, loaderView, processView;

    private boolean isFinished = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_loading, container, false);

        // Initialize views
        finishBtn = view.findViewById(R.id.NextBtn);
        progressBar = view.findViewById(R.id.progressBar);
        nameView = view.findViewById(R.id.NameView);
        typeView = view.findViewById(R.id.TypeView);
        versionView = view.findViewById(R.id.VersionView);
        loaderView = view.findViewById(R.id.LoaderView);
        processView = view.findViewById(R.id.ProcessView);

        // Disable the button initially
        finishBtn.setEnabled(false);
        finishBtn.setText("Loading...");

        // Retrieve arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            nameView.setText(bundle.getString("server_name", "Unknown"));
            typeView.setText(bundle.getString("selected_edition", "N/A"));
            versionView.setText(bundle.getString("selected_version", "N/A"));
            loaderView.setText(bundle.getString("selected_loader", "N/A"));
        }

        simulateProgress();

        // Finish button logic
        finishBtn.setOnClickListener(v -> {
            if (isFinished && isAdded()) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, new HomeFragment()); // Replace with actual container ID
                transaction.addToBackStack(null); // Optional: adds transaction to back stack
                transaction.commit();
            }
        });

        return view;
    }

    private void simulateProgress() {
        new Thread(() -> {
            for (int i = 0; i <= 100; i++) {
                int progress = i;

                handler.post(() -> {
                    if (isAdded()) {
                        progressBar.setProgress(progress);
                        processView.setText("Progress: " + progress + "%");
                    }
                });

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
            }

            handler.post(() -> {
                if (isAdded()) {
                    isFinished = true;
                    finishBtn.setEnabled(true);
                    finishBtn.setText("Finish");
                    processView.setText("Finished");
                }
            });
        }).start();
    }
}
