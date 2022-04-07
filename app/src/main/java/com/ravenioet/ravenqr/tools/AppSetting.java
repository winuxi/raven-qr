package com.ravenioet.ravenqr.tools;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.ravenioet.ravenqr.databinding.SettingsBinding;
public class AppSetting extends Fragment {
    SettingsBinding binding;
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = SettingsBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        binding.eBuuton.setText("Call");
        binding.cButton.setText("Write Email");
        binding.eBuuton.setOnClickListener(view -> {

        });
        binding.cButton.setOnClickListener(view -> {

        });
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}