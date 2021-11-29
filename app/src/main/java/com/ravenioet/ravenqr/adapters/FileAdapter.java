package com.ravenioet.ravenqr.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ravenioet.ravenqr.R;
import com.ravenioet.ravenqr.moels.FileQ;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ContentHolder> {
    private List<FileQ> files = new ArrayList<>();
    public Context context;
    public int request;
    public OnItemClickListener listener;
    View view;

    public FileAdapter(Context context, int request) {
        this.context = context;
        this.request = request;
    }

    @NonNull
    @Override
    public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        return new ContentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentHolder holder, int position) {
        FileQ fileQ = files.get(position);

        String file_inf = "created: " + fileQ.getCreated_at() + ", size: " + fileQ.getFile_size() /*+ ",type: " + fileQ.getFile_ext()*/;
        holder.file_name.setText(fileQ.getFile_name());
        holder.file_inf.setText(file_inf);
        String file_name = internal_storage(context)+"/"+fileQ.getFile_name()+"."+fileQ.getFile_ext();
        holder.qr_pic.setImageBitmap(load_image(file_name));
        holder.share_qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_file(fileQ);
            }
        });
        holder.delete_qr.setOnClickListener(v -> {
            if (delete_file(fileQ)) {
                files.remove(fileQ);
                Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Not Deleted", Toast.LENGTH_LONG).show();
            }
        });
    }

    public Bitmap load_image(String file_path){
        File imgFile = new  File(file_path);
        Log.d("icons",file_path);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_launcher_foreground);
        if(imgFile.exists()){
            icon = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }
        return icon;
    }
    public File internal_storage(Context context) {
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context, null);
        return externalStorageVolumes[0];
    }

    public boolean delete_file(FileQ fileQ) {
        File file = new File(internal_storage(context), fileQ.getFile_name() + "." + fileQ.getFile_ext());
        return file.delete();
    }

    public void share_file(FileQ fileQ) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "RavenIOET Solicit");
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Qr Tool for Managing QR Codes");
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, "Share app via"));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void setFiles(List<FileQ> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public class ContentHolder extends RecyclerView.ViewHolder {
        private final TextView file_name, file_inf;
        private final ImageView qr_pic, share_qr, delete_qr;

        public ContentHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.title);
            file_inf = itemView.findViewById(R.id.info);
            delete_qr = itemView.findViewById(R.id.qr_delete);
            share_qr = itemView.findViewById(R.id.qr_share);
            qr_pic = itemView.findViewById(R.id.icon);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(files.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(FileQ fileQ);
    }

    public void onItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
