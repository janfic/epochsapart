package com.janfic.games.dddserver.epochsapart.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;

public class HexTile extends HexEntity implements Json.Serializable {

    public static int HEX_WIDTH = 64, HEX_HEIGHT = 64;

    // Serialized
    float worldHeight;

    // Unserialized
    TextureRegion region, outline;
    boolean isHovered = false;

    public HexTile() {
        super(0,0,0);
        region = new TextureRegion(new Texture("world/hextiles/tileset.png"), 96, 0, 96, 96);
        outline = new TextureRegion(new Texture("world/hextiles/tileset.png"), 0, 0, 96, 96);
    }

    public HexTile(TextureRegion region, float q, float r, float s, float t) {
        super(q,r,s);
        hexPosition = new Vector3(q, r, s);
        this.region = region;
        this.worldHeight = t;
        setSize(HEX_WIDTH, HEX_HEIGHT);
        setOrigin(region.getRegionWidth() / 2f, region.getRegionHeight() / 2f);
        setHexPosition(q,r,s);
    }

    public HexTile(float q, float r, float s, float t) {
        this(new TextureRegion(new Texture("world/hextiles/tileset.png"), 96, 0, 96, 96), q, r, s, t);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(getColor());
        batch.draw(region, getX() - getOriginX(), getY() - getOriginY() , region.getRegionWidth(), region.getRegionHeight());
        if(isHovered) batch.draw(outline, getX() - getOriginX(), getY() - getOriginY() , outline.getRegionWidth(), outline.getRegionHeight());
        batch.setColor(Color.WHITE);
    }

    // y = (3/4)H * r
    // x = (q - s) * W/2



    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("worldHeight", worldHeight);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        worldHeight = json.readValue("worldHeight", Float.class, jsonData);
        setSize(region.getRegionWidth(), region.getRegionHeight());
        setOrigin(region.getRegionWidth() / 2f, region.getRegionHeight() / 2f);
        updateHexToCartesian();
    }

    public void setHovered(boolean hovered) {
        isHovered = hovered;
    }

    public boolean isHovered() {
        return isHovered;
    }
}
