package com.janfic.games.dddserver.epochsapart.cards;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

//TODO: Add image member
public class Deck<T extends Card> extends Group implements Json.Serializable {
    protected String name;
    protected List<T> cards;

    public Deck() {
        cards = new ArrayList<>();
    }

    public Deck(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<T> getCards() {
        return cards;
    }

    public void addCard(T card) {
        cards.add(card);
        addActor(card);
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void write(Json json) {
        json.writeValue("name", name);
        json.writeValue("cards", cards);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        name = json.readValue("name", String.class, jsonData);
        cards = json.readValue("cards", List.class, jsonData);
//        for (T card : cards) {
//            addActor(card);
//        }
    }
}
