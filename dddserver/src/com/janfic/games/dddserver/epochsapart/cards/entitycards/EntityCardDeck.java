package com.janfic.games.dddserver.epochsapart.cards.entitycards;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.cards.Deck;

import java.util.List;

public class EntityCardDeck extends Deck<EntityCard> {
    public EntityCardDeck() {
        super("Entities");
    }
}
