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
    private int id;

    public Deck() {
        cards = new ArrayList<>();
        image = new Image(new TextureRegion(new Texture("cards/decks/deck_template.png")));
    }

    public Deck(String name) {
        this();
        setName(name);
        this.id = name.hashCode();
    }

    public List<T> getCards() {
        return cards;
    }

    public void addCard(T card) {
        cards.add(card);
        addActor(card);
        card.setDeck(this);
    }

    public void update(float delta) {
        for (T card : cards) {
           card.update(delta);
        }
    }

    public void removeCard(T card) {
        for (T t : cards) {
            if(t.getID() == card.getID()) {
                cards.remove(t);
                card.remove();
                return;
            }
        }
    }

    @Override
    public void write(Json json) {
        json.writeType(getClass());
        json.writeValue("name", getName());
        json.writeValue("cards", cards);
        json.writeValue("id", id);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        json.setTypeName("class");
        id = json.readValue("id", Integer.class, jsonData);
        setName(json.readValue("name", String.class, jsonData));
        List<T> cs = json.readValue("cards", List.class, jsonData);
        for (T card : cs) {
           addCard(card);
        }
    }

    public Image getImage() {
        return image;
    }

    public int getID() {
        return id;
    }

    public T getCardByID(int id) {
        for (T card : cards) {
            if(card.getID() == id) return card;
        }
        return null;
    }
}
