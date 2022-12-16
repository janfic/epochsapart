package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Comparator;
import java.util.Date;

// TODO: Make Game State Change Serializable

/**
 * The Game State Change class helps define and abstract structure in which a change in the game state is made.
 *
 * State changes identify themselves with an automatically generated integer ID and a timestamp. These are useful when
 * state changes are not synced with a server / client.
 *
 * To see how GameStateChanges are validated and applied see: GameRule
 */
public abstract class GameStateChange<T extends GameState> implements Json.Serializable{

    private static Date date = new Date();

    /**
     * Timestamp: client sided.
     */
    protected long timestamp;

    /**
     * Timestamp: server sided.
     */
    protected long serverTimestamp;

    /* Identifying members */
    protected static int idCount;
    protected int id;
    protected int clientID;
    protected int gameID;

    /**
     *  The current status of GameStateChange, set by Game when accepted or rejected
     */
    protected Status status;

    /**
     *  The status of a GameStateChange, set by Game when accepted or rejected
     */
    enum Status {
        PENDING,
        ACCEPTED,
        REJECTED
    }

    public GameStateChange() {

    }

    public GameStateChange(int client_id, int gameID) {
        timestamp = System.currentTimeMillis();
        id = idCount++;
        this.status = Status.PENDING;
        this.clientID = client_id;
        this.gameID = gameID;
    }

    public abstract void applyStateChange(T state);

    public long getTimestamp() {
        return timestamp;
    }

    public int getID() {
        return id;
    }

    public int getClientID() {
        return clientID;
    }

    /**
     * Mark this request as accepted, applied to game state on server.
     */
    public void accept() {
        this.status = Status.ACCEPTED;
    }

    /**
     * Mark this request as rejected, not applied to game state on server.
     */
    public void reject() {
        this.status = Status.REJECTED;
    }

    public void setServerTimestamp(long serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    @Override
    public void write(Json json) {
        json.writeType(this.getClass());
        json.writeValue("timestamp", timestamp);
        json.writeValue("clientID", clientID);
        json.writeValue("gameID", gameID);
        json.writeValue("id", id);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.id = json.readValue("id", Integer.class, jsonData);
        this.clientID = json.readValue("clientID", Integer.class, jsonData);
        this.timestamp = json.readValue("timestamp", Long.class, jsonData);
        this.gameID = json.readValue("gameID", Integer.class, jsonData);
    }

    /**
     * Compares GameStateChanges based on their timestamp.
     */
    public static class GameStateChangeComparator implements Comparator<GameStateChange> {

        @Override
        public int compare(GameStateChange a, GameStateChange b) {
            return (int) Math.signum(a.timestamp - b.timestamp);
        }

    }
}
