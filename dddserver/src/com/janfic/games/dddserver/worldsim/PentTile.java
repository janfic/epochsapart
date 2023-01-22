package com.janfic.games.dddserver.worldsim;

public class PentTile extends WorldTile {

    public PentTile(int index) {
        super(index);
    }

    @Override
    public String toString() {
        return "(p " + index + ")";
    }
}
