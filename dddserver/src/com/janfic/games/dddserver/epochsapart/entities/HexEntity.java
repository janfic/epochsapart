package com.janfic.games.dddserver.epochsapart.entities;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public abstract class HexEntity extends HexActor {

    Inventory inventory;

    public HexEntity() {
        this(0,0,0);
        inventory = new Inventory();
    }

    public HexEntity(float q, float r, float s) {
        super(q,r,s);
        inventory = new Inventory();
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("inventory", inventory);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        inventory = json.readValue("inventory", Inventory.class, jsonData);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
