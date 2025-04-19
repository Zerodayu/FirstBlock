package com.example.firstblock;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {

    private Button createBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        createBtn = view.findViewById(R.id.CreateBtn);

        createBtn.setOnClickListener(v -> {
            Fragment selectTypeFragment = new SelectTypeFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, selectTypeFragment); // use your FrameLayout ID
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
