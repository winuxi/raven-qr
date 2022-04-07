package com.ravenioet.ravenqr.moels;

public class QRFile {
    String file_name;
    String file_size;
    String file_ext;
    String created_at;
    boolean directory;

    public QRFile(String file_name, String file_size, String file_ext, String created_at, boolean directory) {
        this.file_name = file_name;
        this.file_size = file_size;
        this.file_ext = file_ext;
        this.created_at = created_at;
        this.directory = directory;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getFile_ext() {
        return file_ext;
    }

    public void setFile_ext(String file_ext) {
        this.file_ext = file_ext;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }
}
