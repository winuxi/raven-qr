package com.ravenioet.ravenqr.tools;

import android.content.Context;

import androidx.core.content.ContextCompat;

import java.io.File;

public class WorkSpace {
    static WorkSpace workSpace;
    public Context context;
    private String current_dir = "/";
    public String previous_dir = "root";
    public WorkSpace(Context context){
        this.context = context;
    }
    public static WorkSpace getSpace(Context context){
        if(workSpace == null){
            workSpace = new WorkSpace(context);
        }
        return workSpace;
    }
    public File internal_storage() {
        File[] externalStorageVolumes =
                ContextCompat.getExternalFilesDirs(context, null);
        return externalStorageVolumes[0];
    }
    public File getCurrentDir() {
        if(current_dir.equals("/")){
            return internal_storage();
        }
        String path = internal_storage().toString()+"/"+current_dir;
        return new File(path);
    }
    public String getPreviousDir() {
        String path = previous_dir;
        return new File(path).toString();
    }

    public void setCurrentDir(String dir_name, boolean forward) {
        //if(forward)
        previous_dir = current_dir;
        current_dir = dir_name;
    }
}
