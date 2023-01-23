package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Vector3;

public class HexWorld {

    public Polyhedron polyhedron;

    float posX, posY;
    int level;

    public HexWorld(float height, float sx, float sy, int level) {
        posX = sx;
        posY = sy;
        this.level = level;

        polyhedron = new RegularIcosahedron(height);
    }
}
