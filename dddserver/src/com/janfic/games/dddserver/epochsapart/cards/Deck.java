package com.janfic.games.dddserver.epochsapart.cards;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

public class Deck<T extends Card> extends Group implements Json.Serializable {
    protected List<T> cards;
    private Image image;

    public Deck() {
        cards = new ArrayList<>();
        image = new Image(new TextureRegion(new Texture("cards/decks/deck_template.png")));
    }

    public Deck(String name) {
        this();
        setName(name);
    }

    public List<T> getCards() {
        return cards;
    }

    public void addCard(T card) {
        cards.add(card);
        addActor(card);
    }

    @Override
    public void write(Json json) {
        json.writeType(getClass());
        json.writeValue("name", getName());
        json.writeValue("cards", cards);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.setTypeName("class");
        setName(json.readValue("name", String.class, jsonData));
        cards = json.readValue("cards", List.class, jsonData);
    }

    public Image getImage() {
        return image;
    }
}
