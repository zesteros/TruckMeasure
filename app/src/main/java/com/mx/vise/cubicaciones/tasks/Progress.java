package com.mx.vise.cubicaciones.tasks;

public class Progress {
    private int progress;
    private int secondaryProgress;
    private int totalElements;
    private String text;

    public Progress(int progress, int secondaryProgress, int totalElements) {
        this.progress = progress;
        this.secondaryProgress = secondaryProgress;
        this.totalElements = totalElements;
    }

    public Progress(int progress, int secondaryProgress, int totalElements, String text) {
        this.progress = progress;
        this.secondaryProgress = secondaryProgress;
        this.totalElements = totalElements;
        this.text = text;
    }


    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getSecondaryProgress() {
        return secondaryProgress;
    }

    public void setSecondaryProgress(int secondaryProgress) {
        this.secondaryProgress = secondaryProgress;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
