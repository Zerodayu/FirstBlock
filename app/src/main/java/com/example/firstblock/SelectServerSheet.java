package com.example.firstblock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SelectServerSheet extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_server_sheet, container, false);

        LinearLayout serverListLayout = view.findViewById(R.id.SelectServerList);
        TextView placeholder = view.findViewById(R.id.placeholder);

        // Hide placeholder
        if (placeholder != null) {
            placeholder.setVisibility(View.GONE);
        }

        // Load server list from SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("ServerPrefs", Context.MODE_PRIVATE);
        String json = prefs.getString("server_list", null);
        List<ServerData> serverDataList = new ArrayList<>();

        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<ServerData>>() {}.getType();
            serverDataList = gson.fromJson(json, type);
        }

        for (ServerData server : serverDataList) {
            View serverDataView = inflater.inflate(R.layout.server_data, serverListLayout, false);

            // Set name and details
            TextView nameText = serverDataView.findViewById(R.id.ServerName);
            TextView detailsText = serverDataView.findViewById(R.id.serverDetails);

            nameText.setText(server.getName());
            detailsText.setText(server.getEdition() + " | " + server.getVersion() + " | " + server.getLoader());

            // Center-align text
            nameText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            detailsText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            // Optionally, set text to match parent (if you want the text to stretch across the available width)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            nameText.setLayoutParams(params);
            detailsText.setLayoutParams(params);

            // Hide ServerBtns layout
            LinearLayout serverBtns = serverDataView.findViewById(R.id.ServerBtns);
            if (serverBtns != null) {
                serverBtns.setVisibility(View.GONE);
            }

            // Add the server view to the list
            serverListLayout.addView(serverDataView);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            if (parent != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(parent);

                // Keep the original behavior of the bottom sheet
                parent.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int screenHeight = displayMetrics.heightPixels;
                int peekHeight = (int) (screenHeight * 0.5);

                behavior.setPeekHeight(peekHeight);
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                behavior.setSkipCollapsed(false);
            }
        }
    }
}


