package com.janfic.games.dddserver.epochsapart.gamestatechanges;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.dddserver.epochsapart.minigames.inspect.InspectMiniGame;
import com.janfic.games.dddserver.epochsapart.minigames.inventory.InventoryMiniGame;
import com.janfic.games.dddserver.epochsapart.minigames.manageentity.ManageEntityGame;
import com.janfic.games.dddserver.epochsapart.minigames.manageentity.ManageEntityGameState;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.ArrayList;
import java.util.List;

public class CloseSelfMiniGameStateChange extends  GameStateChange<EpochsApartGameState> {

    public long hexID;

    public CloseSelfMiniGameStateChange() {}

    public CloseSelfMiniGameStateChange(long hexID) {
        this.hexID = hexID;
    }

    @Override
    public void applyStateChange(EpochsApartGameState state) {
        List<EpochsApartMiniGame> games = state.getMiniGamesForHexEntity(hexID);
        List<EpochsApartMiniGame> removal = new ArrayList<>();
        for (EpochsApartMiniGame game : games) {
            if(game instanceof InventoryMiniGame || game instanceof ManageEntityGame || game instanceof InspectMiniGame) {
                GameStateChange<EpochsApartGameState> result = game.getResults();
                removal.add(game);
            }
        }

        for (EpochsApartMiniGame epochsApartMiniGame : removal) {
            state.removeMiniGame(epochsApartMiniGame);
            epochsApartMiniGame.getGameState().remove();
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
