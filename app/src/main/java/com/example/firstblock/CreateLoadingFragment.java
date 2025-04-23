package com.example.firstblock;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import java.io.File;
import java.lang.reflect.Type;

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
    private static final String TAG = "CreateLoadingFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_loading, container, false);

        finishBtn = view.findViewById(R.id.FinBtn);
        progressBar = view.findViewById(R.id.progressBar);
        nameView = view.findViewById(R.id.NameView);
        typeView = view.findViewById(R.id.TypeView);
        versionView = view.findViewById(R.id.VersionView);
        loaderView = view.findViewById(R.id.LoaderView);
        processView = view.findViewById(R.id.ProcessView);

        finishBtn.setEnabled(false);
        finishBtn.setText("Loading...");

        bundle = getArguments();
        if (bundle != null) {
            nameView.setText(bundle.getString("server_name", "Unknown"));
            typeView.setText(bundle.getString("selected_edition", "N/A"));
            versionView.setText(bundle.getString("selected_version", "N/A"));
            loaderView.setText(bundle.getString("selected_loader", "N/A"));
        }

        new DownloadServerFilesTask().execute(
                bundle.getString("selected_edition"),
                bundle.getString("selected_version"),
                bundle.getString("selected_loader")
        );

        finishBtn.setOnClickListener(v -> {
            if (isAdded()) {
                if (isFinished && bundle != null) {
                    String serverName = bundle.getString("server_name", "Unknown");
                    String edition = bundle.getString("selected_edition", "N/A");
                    String version = bundle.getString("selected_version", "N/A");
                    String loader = bundle.getString("selected_loader", "N/A");

                    SharedPreferences prefs = requireActivity().getSharedPreferences("ServerPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    Gson gson = new Gson();

                    String json = prefs.getString("server_list", "[]");
                    Type type = new TypeToken<ArrayList<ServerData>>() {}.getType();
                    ArrayList<ServerData> serverList = gson.fromJson(json, type);

                    ServerData newServer = new ServerData(serverName, edition, version, loader);
                    serverList.add(newServer);

                    String updatedJson = gson.toJson(serverList);
                    editor.putString("server_list", updatedJson);
                    editor.apply();

                    Toast.makeText(getContext(), "Server Created: " + serverName, Toast.LENGTH_SHORT).show();
                }

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
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Disable back navigation
            }
        });
    }

    private class DownloadServerFilesTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String edition = params[0];
            String version = params[1];
            String loader = params[2];

            String serverName = bundle.getString("server_name", "Unknown");

            // Create a folder named with the server's name
            File serverFolder = new File(getContext().getFilesDir(), serverName);
            if (!serverFolder.exists()) {
                serverFolder.mkdirs(); // Create the folder
            }

            String downloadUrl = buildDownloadUrl(edition, version, loader);
            Log.d(TAG, "Download URL: " + downloadUrl);

            if (downloadUrl != null) {
                String destinationPath = new File(serverFolder, "minecraft_server_" + version + ".jar").getPath();
                return downloadServerFile(downloadUrl, destinationPath);
            }

            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (isAdded()) {
                int progress = values[0];
                progressBar.setProgress(progress);
                processView.setText("Progress: " + progress + "%");
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
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

        private String buildDownloadUrl(String edition, String version, String loader) {
            String fileName = "Java".equalsIgnoreCase(edition) ? "java_server_links.json" : "bedrock_server_links.json";
            try {
                InputStream inputStream = getContext().getAssets().open(fileName);
                int size = inputStream.available();
                byte[] buffer = new byte[size];
                inputStream.read(buffer);
                inputStream.close();
                String json = new String(buffer, "UTF-8");

                Gson gson = new Gson();
                Type type = new TypeToken<java.util.Map<String, java.util.Map<String, String>>>() {}.getType();
                java.util.Map<String, java.util.Map<String, String>> linksMap = gson.fromJson(json, type);

                if (linksMap.containsKey(version)) {
                    java.util.Map<String, String> loaderMap = linksMap.get(version);
                    if (loaderMap.containsKey(loader)) {
                        return loaderMap.get(loader);
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading JSON file", e);
            }
            return null;
        }

        private boolean downloadServerFile(String urlString, String destinationPath) {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();

                int fileLength = connection.getContentLength();
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(destinationPath);
                byte[] buffer = new byte[1024];
                int total = 0;
                int count;

                while ((count = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, count);
                    total += count;
                    if (fileLength > 0) {
                        publishProgress((int) ((total * 100L) / fileLength));
                    }
                }

                outputStream.close();
                inputStream.close();
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Download error", e);
                return false;
            }
        }
    }
}
