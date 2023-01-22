package com.janfic.games.dddserver.worldsim;

import java.util.List;

public class HexTile extends WorldTile {

    public HexTile(int index) {
        super(index);
    }

    @Override
    public String toString() {
        return "(h " + index + ")";
    }
}
