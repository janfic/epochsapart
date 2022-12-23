package com.janfic.games.dddserver.epochsapart.minigames;

import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.library.utils.gamebuilder.Game;
import com.janfic.games.library.utils.gamebuilder.GameState;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

public abstract class EpochsApartMiniGame<T extends GameState> extends Game<T> {
    public abstract GameStateChange<EpochsApartGameState> getResults();
}
