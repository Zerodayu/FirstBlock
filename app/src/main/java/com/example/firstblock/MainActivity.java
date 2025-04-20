package com.example.firstblock;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.firstblock.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize with the default fragment (HomeFragment)
        replaceFragment(new HomeFragment());

        // Set the listener for the bottom navigation items
        binding.bottomNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Replace fragment based on selected item
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.settings) {
                replaceFragment(new SettingFragment());
            } else if (itemId == R.id.console) {
                replaceFragment(new ConsoleFragment());
            } else if (itemId == R.id.device) {
                replaceFragment(new DeviceFragment());
            } else if (itemId == R.id.info) {
                replaceFragment(new InfoFragment());
            }
            return true; // Return true to confirm item selection handled
        });
    }

    // Method to replace fragment in FrameLayout
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment); // Replace content of FrameLayout
        fragmentTransaction.commit(); // Commit the transaction
    }
}
