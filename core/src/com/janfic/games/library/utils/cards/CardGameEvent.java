package com.janfic.games.library.utils.cards;

import javax.swing.plaf.nimbus.State;

/**
 * A Card Game Event is defined as a movement of data or objects in result of the state of the game matching specific
 * criteria. This differs with Actions. Actions are defined as changes to the state of the game caused directly by
 * a player.
 *
 * An example of the difference can be seen in the common card game of War. Actions are represented by players placing
 * cards down to compare them. An Event is the comparison and collection of these cards once they are both placed
 * ( ie. something that can be automatically done in the game without a certain player performing it. )
 *
 * Events are checked to be triggered after every action in a game. If an event is triggered its affects are applied
 * directly after an action, even if other actions are queued up.
 */
public abstract class CardGameEvent extends StateChange {

    public abstract boolean isTriggered(CardGameState state);
    public abstract void applyStateChange(CardGameState state);

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }
}
