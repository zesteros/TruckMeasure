package com.mx.vise.cubicaciones.tasks;

public class SyncStatus {
    private boolean uploadSuccessful;
    private boolean downloadSuccessful;

    public boolean isUploadSuccessful() {
        return uploadSuccessful;
    }

    public void setUploadSuccessful(boolean uploadSuccessful) {
        this.uploadSuccessful = uploadSuccessful;
    }

    public boolean isDownloadSuccessful() {
        return downloadSuccessful;
    }

    public void setDownloadSuccessful(boolean downloadSuccessful) {
        this.downloadSuccessful = downloadSuccessful;
    }
}
