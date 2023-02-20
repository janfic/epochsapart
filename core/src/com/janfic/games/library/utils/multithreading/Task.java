package com.janfic.games.library.utils.multithreading;

import java.util.ArrayList;
import java.util.List;

public class Task {
    final String name;
    final String description;
    String currentStatus;
    float progress;
    final List<Task> dependencies;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.dependencies = new ArrayList<>();
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
    public void addDependency(Task task){
        dependencies.add(task);
    }

    public boolean isReady() {
        for (Task dependency : dependencies) {
            if(!dependency.isComplete()) return false;
        }
        return true;
    }
}
