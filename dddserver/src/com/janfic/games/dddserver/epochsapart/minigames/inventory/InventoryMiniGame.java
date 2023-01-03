package com.janfic.games.dddserver.epochsapart.minigames.inventory;

import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.cards.actioncards.ActionCard;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.library.utils.gamebuilder.GameClient;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.List;

public class InventoryMiniGame extends EpochsApartMiniGame<InventoryGameState> {

    public InventoryMiniGame() {
        setGameState(new InventoryGameState());
    }

    public InventoryMiniGame(List<HexEntity> entityList) {
        super(entityList);
        setGameState(new InventoryGameState(entityList.get(0).getInventory()));
    }

    @Override
    public void setup() {

    }

    @Override
    public void update(float delta) {

    }

    public void populate(GameClient<EpochsApartGameState> gameClient) {
    }

    @Override
    public GameStateChange<EpochsApartGameState> getResults() {
        return null;
    }
}
