package com.ravenioet.ravenqr.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.ravenioet.ravenqr.moels.QRFile;
public class FileViewModel extends AndroidViewModel {
    private final MutableLiveData<QRFile> fileQMutableLiveData;
    private final MutableLiveData<String> scanResult;
    public FileViewModel(@NonNull Application application) {
        super(application);
        fileQMutableLiveData = new MutableLiveData<>();
        scanResult = new MutableLiveData<>();
    }

    public QRFile getFileQMutableLiveData() {
        return fileQMutableLiveData.getValue();
    }

    public void setFileQMutableLiveData(QRFile QRFile) {
        this.fileQMutableLiveData.setValue(QRFile);
    }
    public String getScanResult() {
        return scanResult.getValue();
    }

    public void setScanResult(String data) {
        this.scanResult.setValue(data);
    }

}