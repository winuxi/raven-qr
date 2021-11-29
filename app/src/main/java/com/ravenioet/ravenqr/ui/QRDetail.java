package com.ravenioet.ravenqr.ui;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravenioet.ravenqr.R;
import com.ravenioet.ravenqr.databinding.DetailFragmentBinding;
import com.ravenioet.ravenqr.moels.FileQ;
import com.ravenioet.ravenqr.view_models.FileViewModel;

import java.io.File;

public class QRDetail extends Fragment {

    private FileViewModel fileViewModel;
    DetailFragmentBinding binding;
    View root;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DetailFragmentBinding.inflate(inflater,container,false);
        root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fileViewModel = new ViewModelProvider(requireActivity()).get(FileViewModel.class);
        FileQ fileQ = fileViewModel.getFileQMutableLiveData();
        binding.qrTitle.setText(fileQ.getFile_name());
        binding.secStatus.setText("Status");
        binding.secValue.setText("Not Secured");
        String file_name = internal_storage(getContext())+"/"+fileQ.getFile_name()+"."+fileQ.getFile_ext();
        binding.qrImage.setImageBitmap(load_image(file_name));
    }
    public File internal_storage(Context context) {
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context, null);
        return externalStorageVolumes[0];
    }

    public Bitmap load_image(String file_path){
        File imgFile = new  File(file_path);
        Log.d("icons",file_path);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher_foreground);
        if(imgFile.exists()){
            icon = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return icon;
    }
}