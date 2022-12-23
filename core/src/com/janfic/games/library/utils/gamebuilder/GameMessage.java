package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.SerializationException;

public class GameMessage {
    public GameMessageType header;
    public String message;
    public int sender;
    public int destination;

    private static Json json = new Json();

    public GameMessage(){};

    public GameMessage(int sender, int destination){
        this.sender = sender;
        this.destination = destination;
    };

    public GameMessage(GameMessageType header, String message, int sender, int destination) {
        this.header = header;
        this.message = message;
        this.sender = sender;
        this.destination = destination;
    }

    public void setHeader(GameMessageType header) {
        this.header = header;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // TODO: Add and Implement new GameMessageTypes
    public enum GameMessageType {
        CONNECTION_INFO,
        GAME_STATE_CHANGE, // TODO
        ACCEPT,
        PING,
        REQUEST_START_GAME, // TODO
        REQUEST_CREATE_GAME, // TODO
        REQUEST_GAMES_INFO, // TODO
        REQUEST_ROOMS_INFO, // TODO
        REQUEST_LEAVE_GAME, // TODO
        REQUEST_JOIN_ROOM, // TODO
        ROOMS_INFO,
        REQUEST_FULL_GAME_STATE, //TODO
        FULL_GAME_STATE,
        DENIED
    }

    public static int[] findEndOfMessage(int start, String data) {
        int firstBrace = data.indexOf('{', start);
        if(firstBrace < 0) return null;
        int braceCount = 0;
        boolean ignore = false;
        for (int i = firstBrace; i < data.length(); i++) {
            if (!ignore) {
                if (data.charAt(i) == '{') {
                    braceCount++;
                } else if (data.charAt(i) == '}') {
                    braceCount--;
                } else if (data.charAt(i) == '"') {
                    ignore = true;
                }
            }
            else if (data.charAt(i) == '"') {
                ignore = false;
            }
            if (braceCount == 0) return new int[] {firstBrace, i+1};
        }
        return null;
    }

    public static GameMessage parse(String string) {
        try {
            return json.fromJson(GameMessage.class, string);
        }
        catch (SerializationException e) {
            return null;
        }
    }

}
