package com.pys.articulo.java.service.facade.status;

public class ProcessStatus {

    private String status;
    private int progress;

    public ProcessStatus(String status, int progress) {
        this.status = status;
        this.progress = progress;
    }

    // Getters y Setters
    public synchronized String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        this.status = status;
    }

    public synchronized int getProgress() {
        return progress;
    }

    public synchronized void setProgress(int progress) {
        this.progress = progress;
    }

}
