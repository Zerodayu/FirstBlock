package com.example.firstblock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONObject;
import org.json.JSONException;

public class ConfirmCreateFragment extends Fragment {

    private TextView typeView, versionView, loaderView, filenameView;
    private EditText serverNameInput;
    private Button nextBtn, backBtn, importBtn;

    private String selectedEdition, selectedVersion, selectedLoader;
    private Uri selectedFileUri = null;

    private static final int PICK_FILE_REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_create, container, false);

        typeView = view.findViewById(R.id.TypeView);
        versionView = view.findViewById(R.id.VersionView);
        loaderView = view.findViewById(R.id.LoaderView);
        serverNameInput = view.findViewById(R.id.serverNameInput);
        nextBtn = view.findViewById(R.id.NextBtn);
        backBtn = view.findViewById(R.id.BackBtn);
        filenameView = view.findViewById(R.id.FilenameView);
        importBtn = view.findViewById(R.id.ImportBtn);

        Bundle bundle = getArguments();
        if (bundle != null) {
            selectedEdition = bundle.getString("selected_edition");
            selectedVersion = bundle.getString("selected_version");
            selectedLoader = bundle.getString("selected_loader");

            typeView.setText(selectedEdition);
            versionView.setText(selectedVersion);
            loaderView.setText(selectedLoader);
        }

        importBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("*/*");  // You can limit to "application/json" if needed
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent, PICK_FILE_REQUEST_CODE); // ADDED
        });

        nextBtn.setOnClickListener(v -> {
            String serverName = serverNameInput.getText().toString().trim();

            SharedPreferences prefs = requireActivity().getSharedPreferences("ServerPrefs", Context.MODE_PRIVATE);
            String json = prefs.getString("server_list", "[]");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ServerData>>() {}.getType();
            ArrayList<ServerData> serverList = gson.fromJson(json, type);

            boolean serverNameExists = false;
            for (ServerData server : serverList) {
                if (server.getName().equals(serverName)) {
                    serverNameExists = true;
                    break;
                }
            }

            if (serverNameExists) {
                Toast.makeText(getContext(), "Server name already exists", Toast.LENGTH_SHORT).show();
            } else if (serverName.isEmpty()) {
                Toast.makeText(getContext(), "Please enter a server name", Toast.LENGTH_SHORT).show();
            } else if (serverName.length() > 10) {
                Toast.makeText(getContext(), "Server name cannot exceed 10 characters", Toast.LENGTH_SHORT).show();
            } else if (serverName.contains(" ")) {
                Toast.makeText(getContext(), "Server name cannot contain spaces", Toast.LENGTH_SHORT).show();
            } else if (selectedFileUri == null) {
                Toast.makeText(getContext(), "Please import a file before proceeding", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Creating the: " + serverName + " Server", Toast.LENGTH_SHORT).show();

                Bundle newBundle = new Bundle();
                newBundle.putString("server_name", serverName);
                newBundle.putString("selected_edition", selectedEdition);
                newBundle.putString("selected_version", selectedVersion);
                newBundle.putString("selected_loader", selectedLoader);
                newBundle.putString("import_file_uri", selectedFileUri.toString());

                Fragment nextFragment = new CreateLoadingFragment();
                nextFragment.setArguments(newBundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.framelayout, nextFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        backBtn.setOnClickListener(v -> requireActivity().onBackPressed());

        return view;
    }

    private void analyzeJarFile(Uri jarUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(jarUri);
            ZipInputStream zipInputStream = new ZipInputStream(inputStream);

            ZipEntry entry;
            String version = null;
            String loader = "Unknown";
            String serverType = "Java";

            Gson gson = new Gson();

            while ((entry = zipInputStream.getNextEntry()) != null) {
                String name = entry.getName();

                // --- Detect Bedrock ---
                if (name.equalsIgnoreCase("bedrock_server") || name.endsWith(".php") || name.equalsIgnoreCase("permissions.json")) {
                    serverType = "Bedrock";
                    loader = "Bedrock";
                }

                // Java Detect loader from manifest
                if (name.equals("META-INF/MANIFEST.MF")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("Main-Class:")) {
                            String mainClass = line.substring("Main-Class:".length()).trim().toLowerCase();
                            if (mainClass.contains("forge")) {
                                loader = "Forge";
                            } else if (mainClass.contains("fabric")) {
                                loader = "Fabric";
                            } else if (mainClass.contains("quilt")) {
                                loader = "Quilt";
                            } else if (mainClass.contains("minecraft")) {
                                loader = "Vanilla";
                            } else if (mainClass.contains("neoforge")) { // Detect NeoForge
                                loader = "NeoForge";
                            }
                        }
                    }
                }

                // Detect Forge installer by presence of install_profile.json
                if (name.equals("install_profile.json")) {
                    loader = "Forge";
                }

                // Detect NeoForge specifically by presence of neo-forge.json or similar
                if (name.equals("neo-forge.json") || name.endsWith("neo-forge.json")) {
                    loader = "NeoForge";
                }

                // Detect Minecraft version from Fabric's fabric.mod.json
                if (name.equals("fabric.mod.json") || name.endsWith("fabric.mod.json")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    StringBuilder jsonBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonBuilder.append(line);
                    }

                    try {
                        String jsonStr = jsonBuilder.toString();
                        JSONObject jsonObject = new JSONObject(jsonStr);

                        // Look for version information
                        if (jsonObject.has("version")) {
                            version = jsonObject.getString("version");
                        }

                        // Check if version matches Minecraft's version format
                        if (version != null && version.matches("\\d+\\.\\d+(\\.\\d+)?")) {
                            break;
                        }

                    } catch (Exception e) {
                        // Skip invalid JSONs
                    }
                }

                // Detect Minecraft version from fabric's install.properties (Fabric-specific)
                if (name.equals("install.properties")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("game-version=")) {
                            version = line.substring("game-version=".length()).trim();
                            break; // Stop reading once we find the game version
                        }
                    }
                }

                // Detect Minecraft version from NeoForge's neo-forge.json
                if (name.equals("neo-forge.json") || name.endsWith("neo-forge.json")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    StringBuilder jsonBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonBuilder.append(line);
                    }

                    try {
                        String jsonStr = jsonBuilder.toString();
                        JSONObject jsonObject = new JSONObject(jsonStr);

                        // Look for NeoForge version information
                        if (jsonObject.has("neoforge_version")) {
                            version = jsonObject.getString("neoforge_version");
                        }

                        // Check if version matches NeoForge version format
                        if (version != null && version.matches("\\d+\\.\\d+(\\.\\d+)?")) {
                            break;
                        }

                    } catch (Exception e) {
                        // Skip invalid JSONs
                    }
                }

                // Detect Minecraft version from any .json file (fallback)
                if (name.endsWith(".json")) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream));
                    StringBuilder jsonBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        jsonBuilder.append(line);
                    }

                    try {
                        String jsonStr = jsonBuilder.toString();
                        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
                        Map<String, Object> jsonMap = gson.fromJson(jsonStr, mapType);

                        // Look for version information in the jsonMap
                        if (jsonMap.containsKey("minecraft")) {
                            version = String.valueOf(jsonMap.get("minecraft"));
                        } else if (jsonMap.containsKey("id")) {
                            version = String.valueOf(jsonMap.get("id"));
                        } else if (jsonMap.containsKey("version")) {
                            version = String.valueOf(jsonMap.get("version"));
                        }

                        if (version != null && version.matches("\\d+\\.\\d+(\\.\\d+)?")) {
                            break;
                        }

                    } catch (Exception e) {
                        // Skip invalid JSONs
                    }
                }

                // Exit early if both loader and version have been found
                if (version != null && !loader.equals("Unknown")) {
                    break;
                }
            }

            zipInputStream.close();

            // If version is still null, set it as Unknown
            if (version == null) version = "Unknown";

            // Show the loader and version information in your UI
            loaderView.setText(loader);
            versionView.setText(version);
            typeView.setText(serverType);

            selectedEdition = serverType;
            selectedLoader = loader;
            selectedVersion = version;

            Log.d("ServerImport", "Auto-set: " + selectedEdition + ", " + selectedLoader + ", " + selectedVersion);


        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to analyze JAR file", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                selectedFileUri = data.getData(); // Save it for later
                String filename = getFileName(selectedFileUri);
                filenameView.setText(filename); // Show file name
                analyzeJarFile(selectedFileUri);
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = requireActivity().getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index >= 0) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result != null ? result.lastIndexOf('/') : -1;
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result != null ? result : "Unknown file";
    }
}