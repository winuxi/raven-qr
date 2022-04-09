package com.ravenioet.ravenqr.view_models;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.ravenioet.ravenqr.moels.QrFile;
import com.ravenioet.ravenqr.tools.ReloadFiles;

public class FileViewModel extends AndroidViewModel {
    private final MutableLiveData<QrFile> fileQMutableLiveData;
    private final MutableLiveData<String> scanResult;
    private ReloadFiles reloadFiles;
    public FileViewModel(@NonNull Application application) {
        super(application);
        fileQMutableLiveData = new MutableLiveData<>();
        scanResult = new MutableLiveData<>();
    }

    public QrFile getFileQMutableLiveData() {
        return fileQMutableLiveData.getValue();
    }

    public void setFileQMutableLiveData(QrFile QRFile) {
        this.fileQMutableLiveData.setValue(QRFile);
    }
    public String getScanResult() {
        return scanResult.getValue();
    }

    public void setScanResult(String data) {
        this.scanResult.setValue(data);
    }

    public ReloadFiles getReloadFiles() {
        return reloadFiles;
    }
    public void setReloadFiles(ReloadFiles reloadFiles) {
        this.reloadFiles = reloadFiles;
    }

}