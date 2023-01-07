package com.janfic.games.dddserver.epochsapart.minigames.inspect;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.library.utils.gamebuilder.GameState;

public class InspectGameState extends GameState {

    HexEntity inspector, inspected;

    public InspectGameState(){}

    @Override
    public void update(float delta) {

    }

    public InspectGameState(HexEntity inspector, HexEntity inspected){
        this();
        this.inspected = inspected;
        this.inspector = inspector;
    }

    @Override
    public void reset() {

    }

    @Override
    public void repopulate(GameState state) {

    }

    @Override
    public void write(Json json) {

    }

    @Override
    public void read(Json json, JsonValue jsonData) {

    }
}
