package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public class GameRoomGroup implements Runnable {
    private final List<GameRoom> gameRooms;
    private List<GameClient> clients;

    private boolean isRunning;

    public GameRoomGroup() {
        this.gameRooms = new ArrayList<>();
        isRunning = false;
    }

    public void addGameRoom(GameRoom room) {
        synchronized (gameRooms) {
            this.gameRooms.add(room);
        }
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int size() {
        return gameRooms.size();
    }

    @Override
    public void run() {
        while(isRunning) {
            //TODO: Write Game Room Loop

            // Update Rooms
            synchronized (gameRooms) {
                for (GameRoom gameRoom : gameRooms) {
                    gameRoom.update(Gdx.graphics.getDeltaTime());
                }
            }
        }
    }

    public List<GameRoom> getGameRooms() {
        return gameRooms;
    }
}
