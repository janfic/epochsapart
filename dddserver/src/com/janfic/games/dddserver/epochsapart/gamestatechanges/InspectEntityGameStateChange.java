package com.janfic.games.dddserver.epochsapart.gamestatechanges;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.dddserver.epochsapart.minigames.inspect.InspectMiniGame;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.ArrayList;
import java.util.List;

public class InspectEntityGameStateChange extends GameStateChange<EpochsApartGameState> {

    private long inspectorID, inspectedID;
    private int miniGameID;

    public InspectEntityGameStateChange() {
        miniGameID = -1;
    }

    public InspectEntityGameStateChange(long inspectorID, long inspectedID) {
        this.inspectedID = inspectedID;
        this.inspectorID = inspectorID;
        miniGameID = -1;
    }

    @Override
    public void applyStateChange(EpochsApartGameState state) {
        HexEntity inspector = (HexEntity) state.getEntityByID(inspectorID);
        HexEntity inspected = (HexEntity) state.getEntityByID(inspectedID);
        List<HexEntity> entityList = new ArrayList<>();
        if(inspector == null || inspected == null) return;
        entityList.add(inspector);
        entityList.add(inspected);
        InspectMiniGame inspectMiniGame = new InspectMiniGame(entityList);
        if(miniGameID < 0) {
            int id = state.nextMiniGameID++;
            inspectMiniGame.setMiniGameID(id);
            miniGameID = id;
        }
        else {
            inspectMiniGame.setMiniGameID(miniGameID);
        }
        state.addMiniGame(inspectMiniGame);
    }

    public long getInspectedID() {
        return inspectedID;
    }

    public long getInspectorID() {
        return inspectorID;
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        inspectorID = json.readValue("inspectorID", Long.class, jsonData);
        inspectedID = json.readValue("inspectedID", Long.class, jsonData);
        miniGameID = json.readValue("miniGameID", Integer.class, jsonData);
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("inspectorID", inspectorID);
        json.writeValue("inspectedID", inspectedID);
        json.writeValue("miniGameID", miniGameID);
    }
}
