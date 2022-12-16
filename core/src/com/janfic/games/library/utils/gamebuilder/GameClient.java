package com.janfic.games.library.utils.gamebuilder;

import com.janfic.games.library.utils.patterns.Observer;

import java.util.LinkedList;
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
        if(!GameServerAPI.getSingleton().messages.isEmpty()) {
            GameMessage message = GameServerAPI.getSingleton().messages.poll();
            if(message == null) return;
            System.out.println(message.header + " " + message.message);
            if(message.header == GameMessage.GameMessageType.CONNECTION_INFO) {
                int id = Integer.parseInt(message.message);
                this.id = id;
            }
        }
    }

    public T getGameState() {
        return gameState;
    }
}
