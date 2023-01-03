package com.janfic.games.dddserver.epochsapart.cards.entitycards;

import com.janfic.games.dddserver.epochsapart.cards.Card;

public abstract class ModifierCard extends Card {

    EntityCard entityCard;

    public ModifierCard() {}

    public ModifierCard(String name) {
        super(name);
    }

    public ModifierCard(String name, EntityCard card) {
        super(name);
        this.entityCard = card;
    }

    public EntityCard getEntityCard() {
        return entityCard;
    }

    public void setEntityCard(EntityCard entityCard) {
        this.entityCard = entityCard;
    }

    public abstract boolean isValidCard(Card card);

    public abstract void modify();
}
