package com.janfic.games.library.utils.gamebuilder;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatGame extends Game<ChatGame.ChatGameState> {

    public static class PostChat extends GameStateChange<ChatGameState> {

        String text;

        public PostChat() {
            super(-1, -1);
        }

        public PostChat(String text, int clientID, int gameID) {
            super(clientID, gameID);
            this.text = text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public void applyStateChange(ChatGameState state) {
            state.chats.add(text);
        }

        @Override
        public void write(Json json) {
            super.write(json);
            json.writeValue("text", text);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            super.read(json, jsonData);
            this.text = json.readValue("text", String.class, jsonData);
        }
    }

    public static class ChatGameState extends GameState {
        List<String> chats;
        public ChatGameState() {
            chats = new ArrayList<>();
        }

        @Override
        public void write(Json json) {
            json.writeValue("chats", chats);
        }

        @Override
        public void read(Json json, JsonValue jsonData) {
            chats = json.readValue("chats", List.class, String.class, jsonData);
        }

        @Override
        public void reset() {

        }

        @Override
        public void repopulate(GameState state) {

        }
    }

    public ChatGame() {
        super(new ChatGameState());
    }

    @Override
    public void setup() {

    }

    @Override
    public void update(float delta) {
        if(getQueuedStateChanges().isEmpty()) return;
        GameStateChange<ChatGameState> gameStateChange = getQueuedStateChanges().poll();
        if(gameStateChange instanceof PostChat) {
            PostChat chatPost = (PostChat) gameStateChange;
            String chatText = "[Client #" + chatPost.clientID + "] : " + chatPost.text;
        }
    }
}
