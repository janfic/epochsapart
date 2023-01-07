package com.janfic.games.library.utils.gamebuilder.realtime;

import com.janfic.games.library.utils.gamebuilder.Game;
import com.janfic.games.library.utils.gamebuilder.GameRule;
import com.janfic.games.library.utils.gamebuilder.GameState;
import com.janfic.games.library.utils.gamebuilder.GameStateChange;

/**
 * A RealTimeGame refers to a game in which actions and events happen in real time. It is done without a "turns" for
 * each entity. ie. Player and entity actions happen at a first come first server basis.
 * @param <T>
 */
public class RealTimeGame<T extends GameState> extends Game<T> {

    public RealTimeGame() {}

    public RealTimeGame(T gameState) {
        super(gameState);
    }

    @Override
    public void setup() {

    }

    @Override
    public void update(float delta) {
        gameState.update(delta);
        if(getQueuedStateChanges().isEmpty()) return;

        GameStateChange<T> gameStateChange = getQueuedStateChanges().poll();
        if(gameStateChange == null) return;

        boolean isValid = false;
        for (GameRule<T> rule : rules) {
            if(rule.isStateChangeValid(gameStateChange, gameState)) {
                isValid = true;
                break;
            }
        }

        if(isValid) {
            gameStateChange.applyStateChange(gameState);
            gameStateChange.accept();
        }
        else {
            gameStateChange.reject();
        }
        getProcessedStateChanges().add(gameStateChange);
    }
}
