package com.janfic.games.library.utils.gamebuilder.turnbased;

import com.janfic.games.library.utils.gamebuilder.*;

import java.util.ArrayList;
import java.util.List;

/**
 *  A TurnBasedGame is an implementation of Game in which it is assumed that GameStateChanges that are requested by
 *  players, are done in a specific order, with discrete steps in gameplay. Such as Chess, Checkers, Poker,
 *  Spades, etc. Unlike, Spoons, Spit, etc. ( See RealTimeGame )
 */
public abstract class TurnBasedGame<T extends GameState> extends Game<T> {

    /** Features a list of game clients to indicate the order of turns.
     *  Instantiated in setup() should be populated in subclass' setup()
     */
    protected List<GameClient<T>> turnOrder;

    /** The current turn as an index of turnOrder **/
    protected int currentTurn;

    public TurnBasedGame(T beginState) {
        super(beginState);
    }

    @Override
    public void setup() {
        this.turnOrder = new ArrayList<>();
        this.currentTurn = 0;
    }

    @Override
    public void update(float delta) {
        if(getQueuedStateChanges().isEmpty()) return;

        GameStateChange<T> gameStateChange = getQueuedStateChanges().poll();
        if(gameStateChange == null) return;

        // Is Valid State Change?
        boolean isValid = true;
        if(gameStateChange.getClientID() == turnOrder.get(0).getID()) {
            for (GameRule<T> rule : rules) {
                if(!rule.isStateChangeValid(gameStateChange, gameState)) {
                    isValid = false;
                    break;
                }
            }
        }
        else {
            isValid = false;
        }

        // Reject or Accept and Apply
        if(isValid) {
            gameStateChange.accept();
            gameStateChange.applyStateChange(gameState);
            currentTurn++;
            if(currentTurn >= turnOrder.size()) {
                currentTurn = 0;
            }
        }
        else {
            gameStateChange.reject();
        }

        // Add to server linked queue
        processedStateChanges.add(gameStateChange);
    }
}
