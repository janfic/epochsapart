package com.janfic.games.dddserver.worldsim;

public class HexWorld {

    public Polyhedron polyhedron0, polyhedron1, polyhedron2;

    float posX, posY;
    int level;

    public HexWorld(float height, float sx, float sy, int level) {
        posX = sx;
        posY = sy;
        this.level = level;

        polyhedron0 = new RegularIcosahedron(height);
        polyhedron1 = Polyhedron.dual(polyhedron0);
        polyhedron2 = Polyhedron.dual(polyhedron1);
    }
}
