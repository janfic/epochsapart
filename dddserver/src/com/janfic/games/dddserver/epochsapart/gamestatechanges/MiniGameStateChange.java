package com.janfic.games.dddserver.epochsapart.gamestatechanges;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.library.utils.gamebuilder.GameState;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.PriorityQueue;

public class MiniGameStateChange<S extends GameState, T extends GameStateChange<S>> extends GameStateChange<EpochsApartGameState> {

    public int miniGameID;
    T gameStateChange;

    public MiniGameStateChange() {
        miniGameID = -1;
    }

    public MiniGameStateChange(int miniGameID, T gameStateChange) {
        this.miniGameID = miniGameID;
        this.gameStateChange = gameStateChange;
    };

    @Override
    public void applyStateChange(EpochsApartGameState state) {
        PriorityQueue<T> priorityQueue = state.getMiniGameByID(miniGameID).getQueuedStateChanges();
        priorityQueue.add(gameStateChange);
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("miniGameID", miniGameID);
        json.writeValue("gameStateChange", gameStateChange);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        gameStateChange = (T) json.readValue("gameStateChange", GameStateChange.class, jsonData);
        miniGameID = json.readValue("miniGameID", Integer.class, jsonData);
    }
}
