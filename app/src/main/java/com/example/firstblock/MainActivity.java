package com.example.firstblock;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;

                case R.id.settings:
                    replaceFragment(new SettingFragment());
                    break;

                case R.id.console:
                    replaceFragment(new ConsoleFragment());
                    break;

                case R.id.device:
                    replaceFragment(new DeviceFragment());
                    break;

                case R.id.info:
                    replaceFragment(new InfoFragment());
                    break;
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment);
        fragmentTransaction.commit();
    }
}