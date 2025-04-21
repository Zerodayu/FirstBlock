package com.example.firstblock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentManager;
import androidx.activity.OnBackPressedCallback;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CreateLoadingFragment extends Fragment {

    private Button finishBtn;
    private ProgressBar progressBar;
    private TextView nameView, typeView, versionView, loaderView, processView;

    private boolean isFinished = false;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_loading, container, false);

        // Initialize views
        finishBtn = view.findViewById(R.id.FinBtn);
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
        bundle = getArguments();
        if (bundle != null) {
            nameView.setText(bundle.getString("server_name", "Unknown"));
            typeView.setText(bundle.getString("selected_edition", "N/A"));
            versionView.setText(bundle.getString("selected_version", "N/A"));
            loaderView.setText(bundle.getString("selected_loader", "N/A"));
        }

        // Start the downloading process
        new DownloadServerFilesTask().execute(bundle.getString("selected_edition"),
                bundle.getString("selected_version"),
                bundle.getString("selected_loader"));

        // Finish button logic
        finishBtn.setOnClickListener(v -> {
            if (isAdded()) {
                if (isFinished) {
                    // Save server and go to Home
                    if (bundle != null) {
                        String serverName = bundle.getString("server_name", "Unknown");
                        String edition = bundle.getString("selected_edition", "N/A");
                        String version = bundle.getString("selected_version", "N/A");
                        String loader = bundle.getString("selected_loader", "N/A");

                        SharedPreferences prefs = requireActivity().getSharedPreferences("ServerPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        Gson gson = new Gson();

                        String json = prefs.getString("server_list", "[]");
                        Type type = new TypeToken<ArrayList<ServerData>>(){}.getType();
                        ArrayList<ServerData> serverList = gson.fromJson(json, type);

                        ServerData newServer = new ServerData(serverName, edition, version, loader);
                        serverList.add(newServer);

                        String updatedJson = gson.toJson(serverList);
                        editor.putString("server_list", updatedJson);
                        editor.apply();

                        Toast.makeText(getContext(), "Server Created: " + serverName, Toast.LENGTH_SHORT).show();
                    }
                }

                // In both success and failure cases, go to Home
                requireActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, new HomeFragment());
                transaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Disable back button while this fragment is active
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Do nothing (disable back navigation)
            }
        });
    }

    // AsyncTask to download server files
    private class DownloadServerFilesTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String edition = params[0];
            String version = params[1];
            String loader = params[2];

            String downloadUrl = buildDownloadUrl(edition, version, loader);
            if (downloadUrl != null) {
                String destinationPath = getContext().getFilesDir() + "/minecraft_server_" + version + ".jar";
                return downloadServerFile(downloadUrl, destinationPath);
            }

            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (isAdded()) {
                int progress = values[0];
                progressBar.setProgress(progress);
                processView.setText("Progress: " + progress + "%");
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                isFinished = true;
                finishBtn.setEnabled(true);
                finishBtn.setText("Finish");
                processView.setText("Finished");
            } else {
                isFinished = false;
                finishBtn.setEnabled(true);
                finishBtn.setText("Home");
                processView.setText("Download failed!");
            }
        }

        // Method to build download URL based on user selection
        private String buildDownloadUrl(String edition, String version, String loader) {
            String baseUrl = "https://example.com/minecraft/"; // Update with actual base URL for Minecraft servers
            if ("Java".equals(edition)) {
                if ("Forge".equals(loader)) {
                    return baseUrl + "forge/" + version + "/forge-" + version + "-universal.jar";
                } else if ("Fabric".equals(loader)) {
                    return baseUrl + "fabric/" + version + "/fabric-api-" + version + ".jar";
                } else {
                    return baseUrl + "vanilla/" + version + "/server.jar";
                }
            } else if ("Bedrock".equals(edition)) {
                return "https://minecraft.net/en-us/download/server/bedrock";
            }
            return null;
        }

        // Download the server file and update progress
        private boolean downloadServerFile(String urlString, String destinationPath) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int fileLength = connection.getContentLength();
                InputStream inputStream = connection.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(destinationPath);
                byte[] buffer = new byte[1024];
                int totalDownloaded = 0;
                int length;

                while ((length = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, length);
                    totalDownloaded += length;
                    int progress = (int) ((totalDownloaded * 100L) / fileLength);
                    publishProgress(progress); // Update progress
                }

                fileOutputStream.close();
                inputStream.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}