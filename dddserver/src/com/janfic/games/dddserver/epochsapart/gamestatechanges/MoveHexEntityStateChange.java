package com.janfic.games.dddserver.epochsapart.gamestatechanges;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.entities.HexActor;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

public class MoveHexEntityStateChange extends GameStateChange<EpochsApartGameState> {

    long hexID;
    Vector3 delta;

    public MoveHexEntityStateChange() {}

    public MoveHexEntityStateChange(long hexID, float dq, float dr, float ds) {
        this.hexID = hexID;
        this.delta = new Vector3(dq,dr,ds);
    }

    public MoveHexEntityStateChange(long hexID, Vector3 delta) {
        this(hexID, delta.x, delta.y, delta.z);
    }

    @Override
    public void applyStateChange(EpochsApartGameState state) {
        HexActor hexActor = state.getEntityByID(hexID);
        hexActor.moveHexPosition(delta.x, delta.y, delta.z);
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("hexID", hexID);
        json.writeValue("delta", delta);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.delta = json.readValue("delta", Vector3.class, jsonData);
        this.hexID = json.readValue("hexID", Integer.class, jsonData);
    }

    public long getHexID() {
        return hexID;
    }

    public Vector3 getDelta() {
        return delta;
    }

    @Override
    public String toString() {
        return "MoveHexEntityStateChange{" +
                "hexID=" + hexID +
                ", delta=" + delta +
                '}';
    }
}
