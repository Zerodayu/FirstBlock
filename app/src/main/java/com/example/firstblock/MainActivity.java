package com.example.firstblock;

import android.os.Bundle;
import android.view.View;
import android.os.Build;
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

        // Make the status bar translucent without changing the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Make the status bar translucent
            getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            // Add padding to the content so it doesn't go under the status bar
            View contentView = findViewById(android.R.id.content);
            contentView.setPadding(0, getStatusBarHeight(), 0, 0);
        }

        // Initialize with the default fragment (HomeFragment)
        replaceFragment(new HomeFragment());

        // Set the listener for the bottom navigation items
        binding.bottomNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

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
            return true;
        });

        // Listen for fragment changes to auto-hide/show bottom nav
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.framelayout);
            updateBottomNavVisibility(currentFragment);
        });
    }

    // Method to replace fragment
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.framelayout, fragment);

        // Only add to back stack if it's not a bottom nav fragment
        if (!(fragment instanceof HomeFragment ||
                fragment instanceof SettingFragment ||
                fragment instanceof ConsoleFragment ||
                fragment instanceof DeviceFragment ||
                fragment instanceof InfoFragment)) {
            transaction.addToBackStack(null);
        }

        transaction.commit();

        // Immediately update nav visibility
        updateBottomNavVisibility(fragment);
    }

    // Show/hide bottom navigation based on current fragment
    private void updateBottomNavVisibility(Fragment fragment) {
        if (fragment instanceof HomeFragment ||
                fragment instanceof SettingFragment ||
                fragment instanceof ConsoleFragment ||
                fragment instanceof DeviceFragment ||
                fragment instanceof InfoFragment) {
            binding.bottomNavView.setVisibility(View.VISIBLE);
        } else {
            binding.bottomNavView.setVisibility(View.GONE);
        }
    }

    // Helper method to get the status bar height
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
