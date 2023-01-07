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
        addActor(inventory);
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("inventory", inventory);
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        inventory.update(delta);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        inventory.remove();
        inventory = json.readValue("inventory", Inventory.class, jsonData);
        addActor(inventory);
    }

    public Inventory getInventory() {
        return inventory;
    }
}
