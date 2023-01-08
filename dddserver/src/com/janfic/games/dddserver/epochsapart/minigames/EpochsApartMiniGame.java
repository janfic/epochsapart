package com.janfic.games.dddserver.epochsapart.minigames;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.library.utils.gamebuilder.Game;
import com.janfic.games.library.utils.gamebuilder.GameClient;
import com.janfic.games.library.utils.gamebuilder.GameState;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;
import com.janfic.games.library.utils.gamebuilder.realtime.RealTimeGame;

import java.util.ArrayList;
import java.util.List;

/**
 * A mini-game in Epochs Apart. The mini-game idea is very vague, it really describes any actions performed that is not
 * related to the overworld, such as inventory management, crafting, buildings, and battles.
 *
 * The information of these mini-games ( such as their internal states ) are only shared with participating clients.
 * Their affects on the world are applied to the world accordingly after the mini-game is complete.
 * @param <T>
 */
public abstract class EpochsApartMiniGame<T extends GameState> extends RealTimeGame<T> implements Json.Serializable {

    public int miniGameID;
    List<HexEntity> hexEntities;

    public EpochsApartMiniGame() {
        hexEntities = new ArrayList<>();
    }

    public EpochsApartMiniGame(List<HexEntity> clients) {
        this();
        hexEntities.addAll(clients);
    }

    public abstract GameStateChange<EpochsApartGameState> getResults();

    public boolean isEntityInvolved(long hexID) {
        for (HexEntity entity : hexEntities) {
            if(entity.getID() == hexID) return true;
        }
        return false;
    }

    @Override
    public void write(Json json) {
        json.setTypeName("class");
        json.writeType(getClass());
        json.setTypeName(null);
//        json.writeArrayStart("hexEntities");
//        for (HexEntity hexEntity : hexEntities) {
//            json.writeValue(hexEntity.getID());
//        }
//        json.writeArrayEnd();
        json.writeValue("hexEntities", hexEntities);
        json.writeValue("miniGameID", miniGameID);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        hexEntities = json.readValue("hexEntities", List.class, jsonData);
        miniGameID = json.readValue("miniGameID", Integer.class, jsonData);
    }

    public abstract void populate(GameClient<EpochsApartGameState> gameClient);

    public int getMiniGameID() {
        return miniGameID;
    }

    public void setMiniGameID(int miniGameID) {
        this.miniGameID = miniGameID;
    }

}
