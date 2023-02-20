package com.janfic.games.library.utils.multithreading;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TaskManager {

    private static TaskManager singleton;
    Queue<Task> taskQueue;
    Queue<OngoingTask> ongoingTaskQueue;
    List<Thread> threads, ongoingThreads;
    int maxTreads = 2;

    private TaskManager() {
        this.taskQueue = new LinkedList<>();
        this.ongoingTaskQueue = new LinkedList<>();
        this.ongoingThreads = new ArrayList<>();
        this.threads = new ArrayList<>();
    }

    public static TaskManager getSingleton() {
        if (singleton == null) singleton = new TaskManager();
        return singleton;
    }

    public void setMaxTreads(int maxTreads) {
        this.maxTreads = maxTreads;
    }

    public void addTask(Task task) {
        if (task instanceof OngoingTask)
            ongoingTaskQueue.add((OngoingTask) task);
        else
            taskQueue.add(task);
    }

    public void update() {
        int diff = maxTreads - threads.size();
        for (int i = 0; i < diff; i++) {
            if (!taskQueue.isEmpty())
                if (taskQueue.peek().isReady()) {
                    final Task task = taskQueue.poll();
                    if (task == null) continue;
                    Thread thread = new Thread(() -> {
                        task.start();
                    });
                    threads.add(thread);
                    thread.start();
                }
        }
        List<Thread> removed = new ArrayList<>();
        for (Thread thread : threads) {
            if (!thread.isAlive()) {
                removed.add(thread);
            }
        }
        if (removed.size() > 0) threads.removeAll(removed);
        if (!ongoingTaskQueue.isEmpty()) {
            if (ongoingTaskQueue.peek().isReady()) {
                final OngoingTask task = ongoingTaskQueue.poll();
                if (task != null) {
                    Thread thread = new Thread(() -> {
                        task.start();
                    });
                    ongoingThreads.add(thread);
                    thread.start();
                }
            }
        }
    }
}
