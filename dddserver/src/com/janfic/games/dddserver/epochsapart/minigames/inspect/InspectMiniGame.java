package com.janfic.games.dddserver.epochsapart.minigames.inspect;

import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.dddserver.epochsapart.entities.HexEntity;
import com.janfic.games.dddserver.epochsapart.minigames.EpochsApartMiniGame;
import com.janfic.games.library.utils.gamebuilder.GameClient;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

import java.util.List;

public class InspectMiniGame extends EpochsApartMiniGame<InspectGameState> {

    public InspectMiniGame(List<HexEntity> entityList) {
        super(entityList);
        setGameState(new InspectGameState());
    }

    @Override
    public void setup() {

    }

    @Override
    public void update(float delta) {

    }

    @Override
    public GameStateChange<EpochsApartGameState> getResults() {
        return null;
    }

    @Override
    public void populate(GameClient<EpochsApartGameState> gameClient) {

    }
}
