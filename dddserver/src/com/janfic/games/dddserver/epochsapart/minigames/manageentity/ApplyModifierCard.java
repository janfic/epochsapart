package com.janfic.games.dddserver.epochsapart.minigames.manageentity;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.cards.Deck;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.EntityCard;
import com.janfic.games.dddserver.epochsapart.cards.entitycards.ModifierCard;
import com.janfic.games.library.utils.gamebuilder.GameServerAPI;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

public class ApplyModifierCard extends GameStateChange<ManageEntityGameState> {

    EntityCard target;
    ModifierCard origin;

    public ApplyModifierCard() {}

    public ApplyModifierCard(EntityCard target, ModifierCard origin) {
        this.target = target;
        this.origin = origin;
    }

    @Override
    public void applyStateChange(ManageEntityGameState state) {
        target = state.entity.getInventory().getEntityCardDeck().getCardByID(target.getID());
        origin = (ModifierCard) state.entity.getInventory().getDeckByID(origin.getDeckID()).getCardByID(origin.getID());
        if(!target.isValidCard(origin)) return;
        Deck deck = state.entity.getInventory().getDeckByID(origin.getDeckID());
        deck.removeCard(origin);
        target.addModifier(origin);
        state.reset();
        state.makeInventory();
        state.makeEntityCardTable();
    }

    @Override
    public void write(Json json) {
        super.write(json);
        json.writeValue("target", target);
        json.writeValue("origin", origin);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        target = json.readValue("target", EntityCard.class, jsonData);
        origin = json.readValue("origin", ModifierCard.class, jsonData);
    }
}
