package com.janfic.games.library.utils.cards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;

import javax.swing.plaf.nimbus.State;
import java.util.Comparator;
import java.util.Date;

public abstract class StateChange implements Comparable<StateChange> {
    protected long timestamp;
    protected int id;
    private static int idCount;
    private final static Date date = new Date();

    public StateChange() {
        this.timestamp = date.getTime();
        this.id = idCount++;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public abstract void applyStateChange(CardGameState cardGameState);

    public int getID() {
        return id;
    }

    @Override
    public int compareTo(StateChange stateChange) {
        return (int) Math.signum(stateChange.timestamp - this.timestamp);
    }

    public static class StateChangeComparator implements Comparator<StateChange> {
        @Override
        public int compare(StateChange a, StateChange b) {
            return  (int) Math.signum(b.getTimestamp() - a.getTimestamp());
        }
    }

    public abstract Action changeAnimation(CardGameState state);
}
