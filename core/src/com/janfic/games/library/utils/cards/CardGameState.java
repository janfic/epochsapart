package com.janfic.games.library.utils.cards;

import java.util.ArrayList;
import java.util.List;

public class CardGameState {
    private List<CardGameActor> actors;
    private List<Card> cards;
    private List<CardSlot> slots;

    public CardGameState() {
        this.actors = new ArrayList<>();
        this.cards = new ArrayList<>();
        this.slots = new ArrayList<>();
    }

    public CardGameState(List<CardGameActor> actors, List<Card> cards, List<CardSlot> slots) {
        this.actors = actors;
        this.cards = cards;
        this.slots = slots;
    }

    public void addActors(List<CardGameActor> actors) {
        this.actors.addAll(actors);
    }

    public void removeActors(List<CardGameActor> actors) {
        this.actors.removeAll(actors);
    }

    public void addCards(List<Card> cards) {
        this.cards.addAll(cards);
    }

    public void removeCards(List<Card> cards) {
        this.cards.removeAll(cards);
    }

    public void addSlots(List<CardSlot> slots) {
        this.slots.addAll(slots);
    }

    public void removeSlots(List<CardSlot> slots) {
        this.slots.removeAll(slots);
    }

    /**
     * @return a copy of the list of actors at this current state.
     */
    public List<Card> getCards() {
        return new ArrayList<>(this.cards);
    }

    /**
     * @return a copy of the list of actors at this current state.
     */
    public List<CardGameActor> getActors() {
        return new ArrayList<>(this.actors);
    }

    @Override
    public String toString() {
        String s = "";
        s += "Actors: " + actors + "\n";
        s += "Slots: " + slots + "\n";
        return s;
    }

    public CardSlot getSlotByID(int id) {
        for (CardSlot slot : slots) {
            if(slot.id == id) return slot;
        }
        return null;
    }
}
