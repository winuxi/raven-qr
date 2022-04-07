package com.ravenioet.ravenqr.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ravenioet.ravenqr.databinding.QrResultBinding;
import com.ravenioet.ravenqr.view_models.FileViewModel;

public class QRScanResult extends Fragment {
    QrResultBinding binding;

    private FileViewModel resultViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = QrResultBinding.inflate(inflater,container,false);
        resultViewModel = new ViewModelProvider(requireActivity()).get(FileViewModel.class);
        load_result();
        return binding.getRoot();
    }
    public void load_result(){
        binding.scanResult.setText(resultViewModel.getScanResult());
    }
}