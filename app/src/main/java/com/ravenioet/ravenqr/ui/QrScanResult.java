package com.ravenioet.ravenqr.ui;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.WriterException;
import com.ravenioet.ravenqr.R;
import com.ravenioet.ravenqr.databinding.QrResultBinding;
import com.ravenioet.ravenqr.tools.ReloadFiles;
import com.ravenioet.ravenqr.tools.WorkSpace;
import com.ravenioet.ravenqr.view_models.FileViewModel;

import java.io.File;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QrScanResult extends Fragment {
    QrResultBinding binding;

    private FileViewModel resultViewModel;
    ReloadFiles reloadFiles;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = QrResultBinding.inflate(inflater,container,false);
        resultViewModel = new ViewModelProvider(requireActivity()).get(FileViewModel.class);
        binding.saveScanned.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        load_result();
        return binding.getRoot();
    }
    public void load_result(){
        reloadFiles = resultViewModel.getReloadFiles();
        binding.scanResult.setText(resultViewModel.getScanResult());
    }
    private void generate_qr() {
        AtomicInteger page = new AtomicInteger();
        Dialog dialog = new Dialog(getContext());
        LayoutInflater customInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customLayout = customInflater.inflate(R.layout.view_qr_create,null);
        dialog.setCanceledOnTouchOutside(true);
        TextView title = customLayout.findViewById(R.id.qr_title);
        MaterialButton back = customLayout.findViewById(R.id.cancel);
        MaterialButton next = customLayout.findViewById(R.id.next);
        title.setText("Generate QR");

        LinearLayout qr_form = customLayout.findViewById(R.id.input_data);
        EditText qr_text = qr_form.findViewById(R.id.text_data);


        LinearLayout qr_result = customLayout.findViewById(R.id.result_qr);
        ImageView imageView = qr_result.findViewById(R.id.qr_image);
        EditText file_name = qr_result.findViewById(R.id.file_name);

        LinearLayout action_btn = customLayout.findViewById(R.id.action_btn);
        LinearLayout status_lay = customLayout.findViewById(R.id.status_log);
        TextView stat_txt = status_lay.findViewById(R.id.stat_txt);

        dialog.setContentView(customLayout);
        //dialog.getWindow().setLayout(width, height);
        dialog.show();
        AtomicReference<Bitmap> bitmap = new AtomicReference<>();
        next.setOnClickListener(v15 -> {
            String location = "Unknown", city = "Unknown";
            if (page.get() == 0) {
                if (qr_text.getText() != null && qr_text.getText().length() > 0) {
                    //below line is for getting the windowmanager service.
                    WindowManager manager = (WindowManager) getContext().getSystemService(WINDOW_SERVICE);
                    //initializing a variable for default display.
                    Display display = manager.getDefaultDisplay();
                    //creating a variable for point which is to be displayed in QR Code.
                    Point point = new Point();
                    display.getSize(point);
                    //getting width and height of a point
                    int width_i = point.x;
                    int height_i = point.y;
                    //generating dimension from width and height.
                    int dimen = Math.min(width_i, height_i);
                    dimen = dimen * 3 / 4;
                    //setting this dimensions inside our qr code encoder to generate our qr code.
                    QRGEncoder qrgEncoder = new QRGEncoder(qr_text.getText().toString(),
                            null, QRGContents.Type.TEXT, dimen);
                    try {
                        //getting our qrcode in the form of bitmap.
                        bitmap.set(qrgEncoder.encodeAsBitmap());
                        // the bitmap is set inside our image view using .setimagebitmap method.
                        imageView.setImageBitmap(bitmap.get());
                    } catch (WriterException e) {
                        //this method is called for exception handling.
                        Log.e("Tag", e.toString());
                    }
                    if (bitmap.get() == null) {
                        stat_txt.setText("Something went wrong");
                    }
                }
            } else if (page.get() == 1) {
                if (file_name.getText() != null && file_name.getText().length() > 0) {
                    city = file_name.getText().toString();
                    stat_txt.setText("Saving");
                    if (save_qr(bitmap.get(), file_name.getText().toString())) {
                        // camera will stop scanning.
                        stat_txt.setText("File Saved");
                        //dialog.dismiss();
                    } else {
                        stat_txt.setText("Cant save file, try again");
                    }
                    //submit_request(dialog, review_btn, review_wait, wait_msg, branch, final_data, city, location);
                } else {
                    stat_txt.setText("Please Write File Name");
                }
            }
        });
    }
    void prepareFile(){

    }

    public boolean save_qr(Bitmap bitmap, String file_name) {
        try {
            String fileName = file_name/*+getCurrentTimeString()*/ + ".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                values.put(MediaStore.MediaColumns.IS_PENDING, 1);
            File file = new File(WorkSpace.getSpace(getContext()).internal_storage(), fileName);
            values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
            Uri uri = getContext().getContentResolver().insert(EXTERNAL_CONTENT_URI, values);

            Log.d("absolute", file.getAbsolutePath() + ", uri: " + uri.toString());
            try (OutputStream output = getActivity().getContentResolver().openOutputStream(uri)) {
                //Bitmap bm = textureView.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                reloadFiles.reloadFiles();
            }
        } catch (Exception e) {
            Log.d("onBtnSavePng", e.toString()); // java.io.IOException: Operation not permitted
            return false;
        }
        return true;
    }
}