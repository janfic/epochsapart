package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SnapshotArray;
import com.janfic.games.library.utils.patterns.Observer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The Game Client refers to a player that interacts with the GameServer to request GameStateChanges ( Moves ) to the
 * game. These clients "observe" ( see Observer pattern ) the game server's StateChange queue. To apply the changes to
 * its own instance of the game state and graphics.
 *
 * @param <T> the type of game state the client observes.
 */
public class GameClient<T extends GameState> extends Observer<Queue<GameStateChange<T>>> {

    T gameState;
    Queue<GameStateChange<T>> queuedGameStateChanges;
    private int id;
    Json json = new Json();

    public GameClient() {
        id = 0;
    }

    public GameClient(int id) {
        this.id = id;
    }

    public GameClient(T gameState, int id) {
        this.id = id;
        this.gameState = gameState;
        this.queuedGameStateChanges = new LinkedList<>();
        this.observedData = new LinkedList<>();
    }

    @Override
    public void observe(Queue<GameStateChange<T>> obj) {
        this.observedData.addAll(obj);
    }

    public int getID() {
        return id;
    }

    public void update(float delta) {
        gameState.update(delta);
        if(!GameServerAPI.getSingleton().messages.isEmpty()) {
            GameMessage message = GameServerAPI.getSingleton().messages.poll();
            if(message == null) return;
            switch (message.header) {
                case CONNECTION_INFO:
                    setID(Integer.parseInt(message.message));
                    break;
                case FULL_GAME_STATE:
                    readFullGameState(message);
                    break;
                case GAME_STATE_CHANGE:
                    receiveGameStateChange(message);
            }
        }
    }

    public void receiveGameStateChange(GameMessage message) {
        GameStateChange<T> change = json.fromJson(GameStateChange.class, message.message);
        if(change.status == GameStateChange.Status.ACCEPTED) {
            change.applyStateChange(gameState);
        }
    }

    public T getGameState() {
        return gameState;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setGameState(T gameState) {
        this.gameState = gameState;
    }

    private void readFullGameState(GameMessage message){
        T state = (T) json.fromJson(gameState.getClass(), message.message);

        SnapshotArray<Actor> children = state.getChildren();
        List<Actor> c = new ArrayList<>();
        for (Actor child : children) {
            c.add(child);
        }
        state.clear();
        gameState.reset();
        for (Actor child : c) {
            this.gameState.addActor(child);
        }
        gameState.repopulate(state);
    }
}
