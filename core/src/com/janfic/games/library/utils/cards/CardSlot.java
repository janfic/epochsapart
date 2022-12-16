package com.janfic.games.library.utils.cards;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a position in a card game in which cards can be placed. ( Like a pile of cards )
 */
public abstract class CardSlot extends Group {
    protected String name;
    protected List<Card> cards;
    protected Consumer<Card> addFirst, addLast;
    protected int id;
    protected boolean isPile;

    protected float messiness;
    protected float stackOffset;

    public CardSlot(String name, Consumer<Card> addFirst, Consumer<Card> addLast, boolean isPile, int id) {
        this.cards = new ArrayList<>();
        this.addFirst = addFirst;
        this.addLast = addLast;
        this.name = name;
        this.id = id;
        this.isPile = isPile;
        this.messiness = 0;
        this.stackOffset = 0;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (Actor child : getChildren()) {
            child.act(delta);
        }
    }

    Random rand = new Random();

    public void addLast(Card card) {
        addActor(card);
        cards.add(card);
        addLast.accept(card);
        card.setRotation((float) ((rand.nextBoolean() ? -Math.random() : Math.random()) * messiness));
        card.setCurrentSlot(this);
        for (int i = cards.size() - 1; i >= 0; i--) {
            cards.get(i).setPosition(0, stackOffset * (cards.size() - i - 1));
        }
        card.setZIndex(0);
    }

    public void addFirst(Card card) {
        addActor(card);
        cards.add(0, card);
        addFirst.accept(card);
        card.setRotation((float) ((rand.nextBoolean() ? -Math.random() : Math.random()) * messiness));
        card.setPosition(0, stackOffset * (cards.size() - 1));
        card.setCurrentSlot(this);
        card.setZIndex(cards.size() + 1);
    }

    public Card popCard() {
        Card c = cards.remove(0);
        removeActor(c);
        c.setCurrentSlot(null);
        return c;
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{[" + name + "](" + id + ")" + cards + "}";
    }

    public int getSize() {
        return cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }

    public boolean isPile() {
        return isPile;
    }

    /**
     * Sets the maximum  ( CCW or CW ) random rotation of a card when in a slot.
     * @param messiness in degrees
     */
    public void setMessiness(float messiness) {
        this.messiness = messiness;
        for (Card card : this.getCards()) {
            card.setRotation((float) ((rand.nextBoolean() ? -Math.random() : Math.random()) * messiness));
        }
    }

    /**
     * Sets the amount of vertical screen space between each card in the pile
     * @param stackOffset in pixels
     */
    public void setStackOffset(float stackOffset) {
        this.stackOffset = stackOffset;
        for (int i = cards.size() - 1; i >= 0; i--) {
            cards.get(i).setPosition(0, stackOffset * (cards.size() - i - 1));
        }
    }
}
