package com.example.firstblock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

public class SettingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        TextView infoBtn = view.findViewById(R.id.InfoBtn);
        TextView playersBtn = view.findViewById(R.id.PlayersBtn);
        TextView modsBtn = view.findViewById(R.id.ModsBtn);
        TextView filesBtn = view.findViewById(R.id.FilesBtn);
        LinearLayout currentServerView = view.findViewById(R.id.CurrentServerView);
        TextView currentServerText = view.findViewById(R.id.currentServerTxt);

        // Handle other button clicks...
        infoBtn.setOnClickListener(v -> {
            Fragment infoFragment = new InfoPanel();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.framelayout, infoFragment)
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

        // Show the SelectServerSheet when currentServerView is clicked
        currentServerView.setOnClickListener(v -> {
            SelectServerSheet bottomSheet = new SelectServerSheet();
            bottomSheet.show(requireActivity().getSupportFragmentManager(), bottomSheet.getTag());
        });

        return view;
    }
}