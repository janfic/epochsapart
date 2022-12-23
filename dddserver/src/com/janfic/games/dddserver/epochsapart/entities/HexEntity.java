package com.janfic.games.dddserver.epochsapart.entities;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.world.HexTile;
import com.janfic.games.library.actions.Action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.janfic.games.dddserver.epochsapart.world.HexTile.HEX_HEIGHT;
import static com.janfic.games.dddserver.epochsapart.world.HexTile.HEX_WIDTH;

public abstract class HexEntity extends Actor implements Json.Serializable{

    public static Set<Long> ids = new HashSet<>();
    protected long id;
    protected Vector3 hexPosition;
    protected int layer;

    public HexEntity() {
        hexPosition = new Vector3(0,0,0);
        layer = 0;
    }

    public HexEntity(float q, float r, float s) {
        assert (q + r + s == 0);
        hexPosition = new Vector3(q,r,s);
        layer = 0;
    }

    public void setHexPosition(float q, float r, float s) {
        hexPosition.set(q,r,s);
        updateHexToCartesian();
    }

    public void setID(long id) {
        this.id = id;
        ids.add(id);
    }

    public void moveHexPosition(float dq, float dr, float ds) {
        hexPosition.add(dq,dr,ds);
        updateHexToCartesian();
    }

    public Vector3 getHexPosition() {
        return hexPosition;
    }

    public void updateHexToCartesian() {
        float q = hexPosition.x;
        float r = hexPosition.y;
        float s = hexPosition.z;
        float x = (-q + s) * (HEX_WIDTH / 2f);
        float y = r * (3 * HEX_HEIGHT / 4f);
        setPosition(x, y);
    }

    // TODO: Implement Hex Entity Actor actions.
    public static class HexActions {
        public static Action hexMoveBy(float q, float r, float s) {
            return null;
        }
    }

    public void generateID() {
        this.id = (long) (Math.random() * Long.MAX_VALUE);
        while(ids.contains(id)) {
            id = (long) (Math.random() * Long.MAX_VALUE);
        }
        ids.add(id);
    }

    @Override
    public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("hexPosition", hexPosition);
        json.writeValue("layer", layer);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        hexPosition = json.readValue("hexPosition", Vector3.class, jsonData);
        id = json.readValue("id", Long.class, jsonData);
        layer = json.readValue("layer", Integer.class, jsonData);
        ids.add(id);
        setID(id);
    }

    public long getID() {
        return id;
    }

    public int getLayer() {
        return layer;
    }
}
