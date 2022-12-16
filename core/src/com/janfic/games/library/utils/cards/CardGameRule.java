package com.janfic.games.library.utils.cards;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

public class CardGameRule<T extends PlayerAction> {

    CardGameActor actor;
    Function<T, Boolean> cardValidator;
    CardSlot slotA, slotB;

    public CardGameRule(CardGameActor actor, CardSlot slotA, CardSlot slotB, Function<T, Boolean> cardValidator) {
        this.actor = actor;
        this.slotA = slotA;
        this.slotB = slotB;
        this.cardValidator = cardValidator;
    }

    public boolean isValid(T action) {
        return cardValidator.apply(action);
    }
}
