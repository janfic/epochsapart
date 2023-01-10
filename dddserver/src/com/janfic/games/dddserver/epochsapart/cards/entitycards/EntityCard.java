package com.janfic.games.dddserver.epochsapart.cards.entitycards;

import com.janfic.games.dddserver.epochsapart.cards.Card;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityCard extends Card {

    List<ModifierCard> cards;

    public EntityCard() {
        cards = new ArrayList<>();
    }

    public EntityCard(String string) {
        super(string);
        cards = new ArrayList<>();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        for (ModifierCard card : cards) {
            card.update(delta);
        }
    }

    public boolean isValidCard(Card card) {
        boolean b = false;
        for (ModifierCard modifierCard : cards) {
            if(modifierCard.isValidCard(card)) b = true;
        }
        return b;
    }

    public List<ModifierCard> getModifierCards() {
        return cards;
    }

    public void addModifier(ModifierCard c){
        cards.add(c);
        c.setEntityCard(this);
        c.modify();
    }
}
