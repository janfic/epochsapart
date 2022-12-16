package com.janfic.games.library.utils.cards;

import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Card extends Actor {
    protected String name;
    protected CardSlot currentSlot;
    protected boolean isFaceUp;

    public Card(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCurrentSlot(CardSlot currentSlot) {
        this.currentSlot = currentSlot;
    }

    @Override
    public void act(float delta) {
    }

    public CardSlot getCurrentSlot() {
        return currentSlot;
    }

    public void setFaceUp(boolean isFaceUp) {
        this.isFaceUp = isFaceUp;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }
}
