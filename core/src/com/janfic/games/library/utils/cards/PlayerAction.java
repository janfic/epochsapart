package com.janfic.games.library.utils.cards;

import java.util.Comparator;
import java.util.Date;

public abstract class PlayerAction extends StateChange {
    private final CardGameActor actor;

    protected PlayerAction(CardGameActor actor) {
        this.actor = actor;
    }

    public CardGameActor getActor() {
        return actor;
    }
}
