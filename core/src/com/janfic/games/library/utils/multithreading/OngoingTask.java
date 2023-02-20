package com.janfic.games.library.utils.multithreading;

public class OngoingTask extends Task {

    public boolean isRunning = true;

    public OngoingTask(String name, String description) {
        super(name, description);
    }

    public void repeatedLogic() {
    }

    @Override
    public void start() {
        while(isRunning) {
            repeatedLogic();
        }
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void stop() {
        isRunning = false;
    }
}
