package com.janfic.games.dddserver.epochsapart.gamestatechanges;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.dddserver.epochsapart.minigames.inventory.InventoryMiniGame;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.ArrayList;
import java.util.List;

public class OpenInventoryMiniGameStateChange extends GameStateChange<EpochsApartGameState> {

    public long hexID;
    public int miniGameID;

    public OpenInventoryMiniGameStateChange() {
        miniGameID = -1;
    }

    public OpenInventoryMiniGameStateChange(long id) {
        this.hexID = id;
        miniGameID = -1;
    }

    @Override
    public void applyStateChange(EpochsApartGameState state) {
        List<HexEntity> entityList = new ArrayList<>();
        entityList.add((HexEntity) state.getEntityByID(hexID));
        InventoryMiniGame miniGame = new InventoryMiniGame(entityList);
        miniGame.setMiniGameID(miniGameID < 0 ? EpochsApartMiniGame.generateID() : miniGameID);
        this.miniGameID = miniGame.getMiniGameID();
        state.addMiniGame(miniGame);
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("hexID", hexID);
        json.writeValue("miniGameID", miniGameID);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        hexID = json.readValue("hexID", Long.class, jsonData);
        miniGameID = json.readValue("miniGameID", Integer.class, jsonData);
    }
}
