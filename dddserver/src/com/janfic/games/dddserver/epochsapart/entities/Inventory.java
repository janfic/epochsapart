package com.janfic.games.dddserver.epochsapart.entities;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.cards.Card;
import com.janfic.games.dddserver.epochsapart.cards.ItemCard;
import com.janfic.games.dddserver.epochsapart.cards.actioncards.ActionCard;
import com.janfic.games.dddserver.epochsapart.cards.actioncards.ActionCardDeck;
import com.janfic.games.dddserver.epochsapart.cards.Deck;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCardDeck;
import com.janfic.games.dddserver.epochsapart.cards.items.ClothItemCard;
import com.janfic.games.dddserver.epochsapart.cards.items.PlantThornsItemCard;

import java.util.ArrayList;
import java.util.List;

public class Inventory extends Table implements Json.Serializable {

    private List<Deck> decks;
    private ActionCardDeck actionCardDeck;
    private EntityCardDeck entityCardDeck;
    private Deck<Card> items;

    public Inventory() {
        decks = new ArrayList<>();
        actionCardDeck = new ActionCardDeck();
        entityCardDeck = new EntityCardDeck();
        items = new Deck<>("Items");
        items.addCard(new ClothItemCard());
        items.addCard(new PlantThornsItemCard());
        actionCardDeck.setName("Action");
        entityCardDeck.setName("Entity");
        addDeck(actionCardDeck);
        addDeck(entityCardDeck);
        addDeck(items);
        setFillParent(true);
        bottom();
        add(actionCardDeck);
        actionCardDeck.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                System.out.println("enter!");
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                System.out.println("exit!");
            }
        });
    }

    public List<Deck> getDecks() {
        return decks;
    }

    public void addDeck(Deck deck) {
        this.decks.add(deck);
        addActor(deck);
        deck.setVisible(false);
    }

    public void update(float delta) {
        for (Deck deck : decks) {
            deck.update(delta);
        }
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
        ActionCardDeck acd = json.readValue("actionCardDeck", ActionCardDeck.class, jsonData);
        EntityCardDeck ecd = json.readValue("entityCardDeck", EntityCardDeck.class, jsonData);
        for (ActionCard card : acd.getCards()) {
            actionCardDeck.addCard(card);
        }
        for (EntityCard card : ecd.getCards()) {
            entityCardDeck.addCard(card);
        }
        for (Deck deck : decks) {
            addActor(deck);
        }
    }

    public Inventory makeCopy() {
        Json json = new Json();
        String d = json.prettyPrint(this);
        System.out.println(d);
        return json.fromJson(Inventory.class, d);
    }

    public ActionCardDeck getActionCardDeck() {
        return actionCardDeck;
    }

    public EntityCardDeck getEntityCardDeck() {
        return entityCardDeck;
    }

    public Deck getDeckByID(int deckID) {
        for (Deck deck : decks) {
            if(deck.getID() == deckID) return deck;
        }
        return null;
    }
}
