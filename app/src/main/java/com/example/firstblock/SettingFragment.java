package com.example.firstblock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        TextView infoBtn = view.findViewById(R.id.InfoBtn);
        TextView setupBtn = view.findViewById(R.id.SetupBtn);
        TextView playersBtn = view.findViewById(R.id.PlayersBtn);
        TextView modsBtn = view.findViewById(R.id.ModsBtn);
        TextView filesBtn = view.findViewById(R.id.FilesBtn);
        TextView backupBtn = view.findViewById(R.id.BackupBtn);
        TextView logBtn = view.findViewById(R.id.LogBtn);
        TextView delBtn = view.findViewById(R.id.DelBtn);
        LinearLayout currentServerView = view.findViewById(R.id.CurrentServerView);


        // Handle other button clicks...
        infoBtn.setOnClickListener(v -> {
            Fragment infoFragment = new InfoPanel();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, infoFragment)
                    .addToBackStack(null)
                    .commit();
        });

        setupBtn.setOnClickListener(v -> {
            Fragment setupFragment = new SetupPanel();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, setupFragment)
                    .addToBackStack(null)
                    .commit();
        });

        playersBtn.setOnClickListener(v -> {
            Fragment playersFragment = new PlayersPanel();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, playersFragment)
                    .addToBackStack(null)
                    .commit();
        });

        modsBtn.setOnClickListener(v -> {
            Fragment modsFragment = new ModsPanel();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, modsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        filesBtn.setOnClickListener(v -> {
            Fragment filesFragment = new FilesPanel();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, filesFragment)
                    .addToBackStack(null)
                    .commit();
        });

        backupBtn.setOnClickListener(v -> {
            Fragment backupFragment = new BackupPanel();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, backupFragment)
                    .addToBackStack(null)
                    .commit();
        });

        logBtn.setOnClickListener(v -> {
            Fragment logFragment = new LogPanel();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, logFragment)
                    .addToBackStack(null)
                    .commit();
        });

        delBtn.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme);

            builder.setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this server?")
                    .setPositiveButton("Delete", (dialogInterface, which) -> {
                        // Handle delete
                    })
                    .setNegativeButton("Cancel", (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        });


        // Show the SelectServerSheet when currentServerView is clicked
        currentServerView.setOnClickListener(v -> {
            SelectServerSheet bottomSheet = new SelectServerSheet();
            bottomSheet.show(requireActivity().getSupportFragmentManager(), bottomSheet.getTag());
        });

        return view;
    }
}