package com.janfic.games.dddserver.epochsapart.minigames.inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.entities.Inventory;
import com.janfic.games.library.utils.gamebuilder.Game;
import com.janfic.games.library.utils.gamebuilder.GameState;

public class InventoryGameState extends GameState {

    Window window;
    Table table, deckACards, deckBCards;
    ScrollPane deckAScroll, deckBScroll;

    Image image;

    Inventory inventory;
    Skin skin;

    public InventoryGameState() {
        skin = new Skin(Gdx.files.internal("assets/ui/skins/default/skin/uiskin.json"));
        image = new Image(new Texture("dot.png"));
        addActor(image);
        window = new Window("Inventory", skin);
        addActor(window);
        window.setKeepWithinStage(false);
    }

    public InventoryGameState(Inventory inventory) {
        this();
        this.inventory = inventory;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        window.setSize(getStage().getWidth()/2, getStage().getHeight() /2);
    }

    @Override
    public void write(Json json) {
        json.writeValue("inventory", inventory);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.inventory = json.readValue("inventory", Inventory.class, jsonData);
    }

    @Override
    public void reset() {

    }

    @Override
    public void repopulate(GameState state) {

    }
}
