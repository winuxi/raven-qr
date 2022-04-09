package com.ravenioet.ravenqr.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.ravenioet.ravenqr.R;
import com.ravenioet.ravenqr.databinding.DetailFragmentBinding;
import com.ravenioet.ravenqr.moels.QrFile;
import com.ravenioet.ravenqr.view_models.FileViewModel;

import java.io.File;

public class QrDetail extends Fragment {

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
        QrFile QRFile = fileViewModel.getFileQMutableLiveData();
        binding.qrTitle.setText(QRFile.getFile_name());
        binding.secStatus.setText("Security Status");
        binding.secValue.setText("Not Secured");
        String file_name = internal_storage(getContext())+"/"+ QRFile.getFile_name()+"."+ QRFile.getFile_ext();
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
            readQR(imgFile.getAbsolutePath());
        }
        return icon;
    }
    public void readQR(String url){
        binding.dataValue.setText(url);
        Bitmap bitmap = BitmapFactory.decodeFile(url);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width*height];
        bitmap.getPixels(pixels,0,width,0,0,width,height);
        bitmap.recycle();

        RGBLuminanceSource source = new RGBLuminanceSource(width,height,pixels);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();
        try {
            Result result = reader.decode(binaryBitmap);
            binding.dataValue.setText(result.getText());
        } catch (NotFoundException e) {
            e.printStackTrace();
            binding.dataValue.setText(e.getMessage());
        }


    }
}