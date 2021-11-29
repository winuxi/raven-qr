package com.ravenioet.ravenqr.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.ravenioet.ravenqr.moels.FileQ;
public class FileViewModel extends AndroidViewModel {
    private final MutableLiveData<FileQ> fileQMutableLiveData;
    private final MutableLiveData<String> scanResult;
    public FileViewModel(@NonNull Application application) {
        super(application);
        fileQMutableLiveData = new MutableLiveData<>();
        scanResult = new MutableLiveData<>();
    }

    public FileQ getFileQMutableLiveData() {
        return fileQMutableLiveData.getValue();
    }

    public void setFileQMutableLiveData(FileQ fileQ) {
        this.fileQMutableLiveData.setValue(fileQ);
    }
    public String getScanResult() {
        return scanResult.getValue();
    }

    public void setScanResult(String data) {
        this.scanResult.setValue(data);
    }

}