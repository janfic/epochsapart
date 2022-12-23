package com.janfic.games.dddserver.epochsapart.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.library.utils.gamebuilder.GameClient;

public class Player extends HexEntity implements Json.Serializable{

    TextureRegion region;

    public Player() {
        this(-1, 0,0,0);
        region = new TextureRegion(new Texture("world/hextiles/player.png"));
    }

    public Player(long clientID, float q, float r, float s) {
        super(q, r, s);
        region = new TextureRegion(new Texture("world/hextiles/player.png"));
        setSize(region.getRegionWidth(), region.getRegionHeight());
        setOrigin(getWidth() / 2, getHeight() / 2);
        setID(clientID);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor());
        batch.draw(region, getX() - getOriginX(), getY() - getOriginY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }

    @Override
    public void write(Json json) {
        super.write(json);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
    }
}
