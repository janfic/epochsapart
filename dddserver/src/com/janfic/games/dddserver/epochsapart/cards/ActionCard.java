package com.janfic.games.dddserver.epochsapart.cards;

import com.janfic.games.dddserver.epochsapart.EpochsApartGame;
import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.entities.HexActor;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.library.actions.Action;
import com.janfic.games.library.utils.gamebuilder.GameClient;
import com.janfic.games.library.utils.gamebuilder.GameState;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class ActionCard extends Card {
    private boolean isActive;
    private HexEntity fromEntity;
    private HexActor target;

    public ActionCard() {
    }

    public ActionCard(String name) {
        super(name);
    }

    public void setFromEntity(HexEntity fromEntity) {
        this.fromEntity = fromEntity;
    }

    public void setTarget(HexActor target) {
        this.target = target;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public HexActor getTarget() {
        return target;
    }

    public HexEntity getFromEntity() {
        return fromEntity;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public abstract GameStateChange<EpochsApartGameState> performAction(GameClient client, EpochsApartGameState state);
}
