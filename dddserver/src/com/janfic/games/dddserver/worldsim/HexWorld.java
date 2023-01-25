package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Matrix4;

public class HexWorld {

    public Polyhedron polyhedron, polyhedron0, polyhedron1, polyhedron2;

    float posX, posY;
    int level;
    public float height;

    public HexWorld(float height, float sx, float sy, int level) {
        posX = sx;
        posY = sy;
        this.height = height;
        this.level = level;

        polyhedron0 = new RegularIcosahedron(height);
        //polyhedron1 = Polyhedron.uniformTruncate(polyhedron0);
        polyhedron2 = polyhedron0;
        for(int i = 1; i <= level; i++) {
            polyhedron2 = Polyhedron.dual(polyhedron2);
            polyhedron2 = Polyhedron.uniformTruncate(polyhedron2);
        }
//        polyhedron = Polyhedron.sphereProject(polyhedron2, height / 2);
        polyhedron = polyhedron2;
    }

    public void dual() {
        polyhedron = Polyhedron.dual(polyhedron);
    }

    public void truncate() {
        polyhedron = Polyhedron.uniformTruncate(polyhedron);
    }

    public void sphere() {
        polyhedron = Polyhedron.sphereProject(polyhedron, height / 2);
    }

    public void reset() {
        polyhedron = new RegularIcosahedron(height);
    }
}
