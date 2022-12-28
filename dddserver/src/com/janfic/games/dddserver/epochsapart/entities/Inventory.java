package com.janfic.games.dddserver.epochsapart.entities;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.cards.ActionCard;
import com.janfic.games.dddserver.epochsapart.cards.ActionDeck;
import com.janfic.games.dddserver.epochsapart.cards.Deck;
import com.janfic.games.dddserver.epochsapart.cards.EntityCard;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends Group implements Json.Serializable {

    private List<Deck> decks;
    private ActionDeck actionCardDeck;
    private Deck<EntityCard> entityCardDeck;

    public Inventory() {
        decks = new ArrayList<>();
        actionCardDeck = new ActionDeck();
        entityCardDeck = new Deck<>();
        actionCardDeck.setName("Action");
        entityCardDeck.setName("Entity");
        addDeck(actionCardDeck);
        addDeck(entityCardDeck);
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public void addDeck(Deck deck) {
        this.decks.add(deck);
        addActor(deck);
    }

    @Override
    public void write(Json json) {
        json.writeValue("decks", decks);
        json.writeValue("actionCardDeck", actionCardDeck);
        json.writeValue("entityCardDeck", entityCardDeck);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        decks = json.readValue("decks", List.class, Deck.class, jsonData);
        actionCardDeck = json.readValue("actionCardDeck", ActionDeck.class, jsonData);
        entityCardDeck = json.readValue("entityCardDeck", Deck.class, jsonData);
    }

    public ActionDeck getActionCardDeck() {
        return actionCardDeck;
    }

    public Deck<EntityCard> getEntityCardDeck() {
        return entityCardDeck;
    }
}
