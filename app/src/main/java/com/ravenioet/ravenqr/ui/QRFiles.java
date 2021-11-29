package com.ravenioet.ravenqr.ui;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.format.Formatter;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.button.MaterialButton;
import com.google.zxing.WriterException;
import com.ravenioet.ravenqr.R;
import com.ravenioet.ravenqr.adapters.FileAdapter;
import com.ravenioet.ravenqr.databinding.HomeBinding;
import com.ravenioet.ravenqr.databinding.ViewQrScannerBinding;
import com.ravenioet.ravenqr.moels.FileQ;
import com.ravenioet.ravenqr.tools.AnimateView;
import com.ravenioet.ravenqr.tools.WorkSpace;
import com.ravenioet.ravenqr.view_models.FileViewModel;

import java.io.File;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.scanner.decoder.zxing.ZXDecoder;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class QRFiles extends Fragment {

    private HomeBinding binding;
    public static List<FileQ> file_store = new ArrayList<>();
    public static FileAdapter fileAdapter;
    View root;
    FileViewModel fileViewModel;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = HomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        fileViewModel = new ViewModelProvider(requireActivity()).get(FileViewModel.class);
        binding.fileLists.setLayoutManager(new LinearLayoutManager(getContext()));
        fileAdapter = new FileAdapter(getContext(), 1);
        return root;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fileLists.setAdapter(fileAdapter);
        load_flies(getContext());
        fileAdapter.onItemClickListener(fileQ -> {
            fileViewModel.setFileQMutableLiveData(fileQ);
            Navigation.findNavController(view).navigate(R.id.detail);
        });
        binding.btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                generate_qr();
            }
        });
        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vie) {
                scan_qr(view);
            }
        });

        // adding listener to the button
        binding.fabScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_qr(view);
                //Toast.makeText(getContext(), "Permission Granted..", Toast.LENGTH_SHORT).show();
            }
        });
        binding.fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate_qr();
            }
        });
/*
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.item_detail_container, holder);
        fragmentTransaction.commit();*/

    }

    public static void load_flies(Context context) {
        file_store.clear();
        String path = WorkSpace.getSpace(context).getCurrentDir().toString();
        Log.d("current at:", path);
        File directory = new File(path);
        File[] files_ar = directory.listFiles(File::isFile);
        List<File> files;
        if (files_ar != null) {
            Log.d("loaded files: ", "Size: " + files_ar.length);
        } else {
            Log.d("loaded files: ", "Size: 0");
        }
        if (files_ar != null) {
            files = new ArrayList<>(Arrays.asList(files_ar));
            FileQ filew;
            for (File file : files) {
                Date lastModified = new Date(file.lastModified());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String formattedDateString = formatter.format(lastModified);
                String file_size = Formatter.formatShortFileSize(context, file.length());
                String file_name = file.getName();
                String file_ext = "Unknown";
                boolean type = file.isDirectory();
                if (!file.getName().split("\\.")[1].equals("jpg")) {
                    continue;
                }
                if (file.isDirectory()) {
                    continue;
                    //int child = file.listFiles().length;
                    //file_ext = String.valueOf(child);
                }
                if (file.getName().contains(".")) {
                    file_name = file.getName().split("\\.")[0];
                    file_ext = file.getName().split("\\.")[1];
                }
                filew = new FileQ(file_name, file_size, file_ext, formattedDateString, type);
                Log.d("Files", "FileName:" + file.getName() +
                        ", size: " + file_size
                        + ", updated: " + formattedDateString);
                file_store.add(filew);
            }
        }

        //File[] files = directory.listFiles();
        fileAdapter.setFiles(file_store);
    }

    private void scan_qr(View view) {
        ViewQrScannerBinding scannerBinding;
        Dialog dialog = new Dialog(getContext());
        scannerBinding = ViewQrScannerBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setCanceledOnTouchOutside(false);
        MaterialButton cancel = scannerBinding.cancel;
        MaterialButton next = scannerBinding.next;
        scannerBinding.qrTitle.setText(R.string.qr_scan);
        scannerBinding.qrMessage.setText(R.string.scan_result);
        //scannerBinding.messageHolder.setVisibility(View.GONE);
        scannerBinding.camview.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onScannerStarted(ScannerLiveView scanner) {
                // method is called when scanner is started
                //Toast.makeText(getContext(), "Scanner Started", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerStopped(ScannerLiveView scanner) {
                // method is called when scanner is stoped.
                //Toast.makeText(getContext(), "Scanner Stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onScannerError(Throwable err) {
                // method is called when scanner gives some error.
                Toast.makeText(getContext(), "Scanner Error: " + err.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeScanned(String data) {
                // method is called when camera scans the
                // qr code and the data from qr code is
                // stored in data in string format.
                //scannerBinding.messageHolder.setVisibility(View.VISIBLE);
                //scannerBinding.scanner.setVisibility(View.GONE);

                scannerBinding.camview.stopScanner();
                dialog.dismiss();
                fileViewModel.setScanResult(data);
                Navigation.findNavController(view).navigate(R.id.result);
                scannerBinding.qrMessage.setText(data);
            }
        });

        ZXDecoder decoder = new ZXDecoder();
        // 0.5 is the area where we have
        // to place red marker for scanning.
        decoder.setScanAreaPercent(0.8);
        // below method will set secoder to camera.
        scannerBinding.camview.setDecoder(decoder);
        scannerBinding.camview.startScanner();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
        dialog.setContentView(scannerBinding.getRoot());
        dialog.getWindow().setLayout(width, height);
        dialog.show();
        next.setOnClickListener(v15 -> {
            // camera will stop scanning.
            scannerBinding.camview.stopScanner();
            dialog.dismiss();
        });
        cancel.setOnClickListener(v15 -> {
            // camera will stop scanning.
            scannerBinding.camview.stopScanner();
            dialog.dismiss();
        });
    }

    private void generate_qr() {
        AtomicInteger page = new AtomicInteger();
        Dialog dialog = new Dialog(getContext());
        LayoutInflater customInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View customLayout = customInflater.inflate(R.layout.view_qr_create, root.findViewById(R.id.toor));
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

        if (page.get() == 0) {
            next.setText("Next");
            back.setText("Cancel");
            qr_result.setVisibility(View.GONE);
            qr_form.setVisibility(View.VISIBLE);
        }


        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.75);
        dialog.setContentView(customLayout);
        //dialog.getWindow().setLayout(width, height);
        dialog.show();
        AtomicReference<Bitmap> bitmap = new AtomicReference<>();
        next.setOnClickListener(v15 -> {
            String location = "Unknown", city = "Unknown";
            if (page.get() == 0) {
                if (qr_text.getText() != null && qr_text.getText().length() > 0) {
                    //city_lay.setError(null);
                    page.set(1);
                    title.setText("Save QR");
                    next.setText("Next");
                    back.setText("Back");
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
                    if (bitmap.get() != null) {
                        animate_to_left(qr_form, qr_result);
                    } else {
                        stat_txt.setText("Something went wrong");
                        animate_to_left(action_btn, status_lay);
                        animate_wait_left(dialog, action_btn, status_lay, false, 0);
                    }
                } else {
                    stat_txt.setText("Please Write Something");
                    animate_to_left(action_btn, status_lay);
                    animate_wait_left(dialog, action_btn, status_lay, false, 0);
                }
            } else if (page.get() == 1) {
                next.setText("Save");
                title.setText("Save QR");
                back.setText("Back");
                if (file_name.getText() != null && file_name.getText().length() > 0) {
                    city = file_name.getText().toString();
                    stat_txt.setText("Saving");
                    animate_to_left(action_btn, status_lay);
                    if (save_qr(bitmap.get(), file_name.getText().toString())) {
                        // camera will stop scanning.
                        stat_txt.setText("File Saved");
                        animate_to_left(action_btn, status_lay);
                        animate_wait_left(dialog, action_btn, status_lay, true, 0);
                        //dialog.dismiss();
                    } else {
                        stat_txt.setText("Cant save file, try again");
                        animate_to_left(action_btn, status_lay);
                        animate_wait_left(dialog, action_btn, status_lay, false, 0);
                    }
                    //submit_request(dialog, review_btn, review_wait, wait_msg, branch, final_data, city, location);
                } else {
                    stat_txt.setText("Please Write File Name");
                    animate_to_left(action_btn, status_lay);
                    animate_wait_left(dialog, action_btn, status_lay, false, 0);
                }
            }
        });
        back.setOnClickListener(v15 -> {
            switch (page.get()) {
                case 0:
                    dialog.dismiss();
                    break;
                case 1:
                    next.setText("Next");
                    back.setText("Cancel");
                    page.set(0);
                    animate_to_right(qr_form, qr_result);
                    break;
            }
        });
    }

    public void animate_to_left(LinearLayout btn, LinearLayout wait) {
        wait.setVisibility(View.VISIBLE);
        wait.setBackgroundColor(Color.WHITE);
        btn.startAnimation(AnimateView.outToLeftAnimation(500));
        wait.startAnimation(AnimateView.inFromRightAnimation(500));
        btn.setVisibility(View.GONE);
    }

    public void animate_to_right(LinearLayout btn, LinearLayout wait) {
        btn.setVisibility(View.VISIBLE);
        wait.setBackgroundColor(Color.WHITE);
        btn.startAnimation(AnimateView.inFromLeftAnimation(500));
        wait.startAnimation(AnimateView.outToRightAnimation(500));
        wait.setVisibility(View.GONE);
    }


    public void animate_wait_left(Dialog dialog, LinearLayout btn, LinearLayout wait, boolean success, int i) {
        if (success) {
            wait.setBackgroundColor(Color.GREEN);
        } else {
            if (i == 1) {
                wait.setBackgroundColor(Color.WHITE);
            } else {
                wait.setBackgroundColor(Color.RED);
            }
        }
        new Handler(Looper.getMainLooper()).postDelayed((Runnable) () -> {
            btn.setVisibility(View.VISIBLE);
            btn.startAnimation(AnimateView.inFromLeftAnimation(500));
            wait.startAnimation(AnimateView.outToRightAnimation(500));
            wait.setVisibility(View.GONE);
            if (success)
                dialog.dismiss();
        }, 3000);
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

            Log.d("absolute",file.getAbsolutePath()+", uri: "+uri.toString());
            try (OutputStream output = getActivity().getContentResolver().openOutputStream(uri)) {
                //Bitmap bm = textureView.getBitmap();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                load_flies(getContext());
            }
        } catch (Exception e) {
            Log.d("onBtnSavePng", e.toString()); // java.io.IOException: Operation not permitted
            return false;
        }
        return true;
    }


}