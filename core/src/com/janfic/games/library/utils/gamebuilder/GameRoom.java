package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.library.utils.patterns.Observable;

import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class GameRoom implements Json.Serializable {

    private final GameServer parentServer;
    private final Game game;
    private final int roomID;
    private final List<GameClient> gameClients;
    protected PriorityQueue<GameStateChange> clientGameStateChangeRequestQueue;

    private Json json;

    private static int roomCount;

    public GameRoom(Game game, GameServer parentServer, List<GameClient> gameClients) {
        this.game = game;
        this.gameClients = gameClients;
        this.parentServer = parentServer;
        this.clientGameStateChangeRequestQueue = new PriorityQueue<>(new GameStateChange.GameStateChangeComparator());
        this.json = new Json();
        this.roomID = roomCount++;
    }

    // TODO: Implement GameRoom update()
    public void update(float delta) {
        // Update Game
        if(!clientGameStateChangeRequestQueue.isEmpty())
            game.getQueuedStateChanges().add(clientGameStateChangeRequestQueue.poll());

        game.update(delta);
        // Give server processed game state changes

        if(!game.getProcessedStateChanges().isEmpty()) {
            // TODO: Add Json-ification of GameStateChange to outgoing messages
            String stateChange = json.toJson(game.getProcessedStateChanges().poll());

            for (GameClient gameClient : gameClients) {
                GameMessage message = new GameMessage(GameMessage.GameMessageType.GAME_STATE_CHANGE, stateChange, 0, gameClient.getID());
                System.out.println("[SERVER]: Sending message: " + message.header + " " + message.message);
                parentServer.addOutgoingMessage(message);
            }
        }
    }

    public Class<? extends Game> getCurrentGameType() {
        return game.getClass();
    }

    public void addClient(GameClient gameClient) {
        this.gameClients.add(gameClient);
    }

    public List<GameClient> getGameClients() {
        return gameClients;
    }

    public int getRoomID() {
        return roomID;
    }

    public void addGameStateChange(GameStateChange gameStateChange) {
        game.getQueuedStateChanges().add(gameStateChange);
    }

    @Override
    public void write(Json json) {
        json.writeValue("id", roomID);
        json.writeValue("gameName", game.getClass().getSimpleName());
    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }

    public Game getGame() {
        return game;
    }
}