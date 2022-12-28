package com.janfic.games.dddserver.epochsapart.minigames;

import com.janfic.games.dddserver.epochsapart.EpochsApartGameState;
import com.janfic.games.library.utils.gamebuilder.Game;
import com.janfic.games.library.utils.gamebuilder.GameState;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

/**
 * A mini-game in Epochs Apart. The mini-game idea is very vague, it really describes any actions performed that is not
 * related to the overworld, such as inventory management, crafting, buildings, and battles.
 *
 * The information of these mini-games ( such as their internal states ) are only shared with participating clients.
 * Their affects on the world are applied to the world accordingly after the mini-game is complete.
 * @param <T>
 */
public abstract class EpochsApartMiniGame<T extends GameState> extends Game<T> {

    public abstract GameStateChange<EpochsApartGameState> getResults();
}
