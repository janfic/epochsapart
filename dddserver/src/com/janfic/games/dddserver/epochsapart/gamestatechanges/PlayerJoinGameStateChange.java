package com.janfic.games.dddserver.epochsapart.gamestatechanges;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.entities.Player;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

public class PlayerJoinGameStateChange extends GameStateChange<EpochsApartGameState> {

    private long hexID;

    public PlayerJoinGameStateChange() {
        hexID = -1;
    }

    public PlayerJoinGameStateChange(long playerHexID) {
        this.hexID = playerHexID;
    }

    @Override
    public void applyStateChange(EpochsApartGameState state) {
        Player p = new Player();
        p.setID(hexID);
        state.addHexEntity(p);
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("hexID", hexID);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        hexID = json.readValue("hexID", Long.class, jsonData);
    }
}
