package com.janfic.games.library.utils.multithreading;

public class Task {
    final String name;
    final String description;
    String currentStatus;
    float progress;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public boolean isComplete(){
        return progress >= 1;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public synchronized float getProgress() {
        return progress;
    }

    public synchronized String getCurrentStatus() {
        return currentStatus;
    }

    public synchronized void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public synchronized void setProgress(float progress) {
        this.progress = progress;
    }

    public void start() {

    }
}
