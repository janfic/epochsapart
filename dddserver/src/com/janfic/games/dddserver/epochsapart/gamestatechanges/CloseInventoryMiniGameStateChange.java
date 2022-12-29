package com.janfic.games.dddserver.epochsapart.gamestatechanges;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.dddserver.epochsapart.minigames.inventory.InventoryMiniGame;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.ArrayList;
import java.util.List;

public class CloseInventoryMiniGameStateChange extends  GameStateChange<EpochsApartGameState> {

    public long hexID;

    public CloseInventoryMiniGameStateChange() {}

    public CloseInventoryMiniGameStateChange(long hexID) {
        this.hexID = hexID;
    }

    @Override
    public void applyStateChange(EpochsApartGameState state) {
        List<EpochsApartMiniGame> games = state.getMiniGamesForHexEntity(hexID);
        List<EpochsApartMiniGame> removal = new ArrayList<>();
        for (EpochsApartMiniGame game : games) {
            if(game instanceof InventoryMiniGame) {
                GameStateChange<EpochsApartGameState> result = game.getResults();
                removal.add(game);
            }
        }

        for (EpochsApartMiniGame epochsApartMiniGame : removal) {
            state.removeMiniGame(epochsApartMiniGame);
            state.removeActor(epochsApartMiniGame.getGameState());
        }
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
