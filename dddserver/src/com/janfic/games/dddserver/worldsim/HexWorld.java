package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Matrix4;

public class HexWorld {

    public Polyhedron polyhedron0, polyhedron1, polyhedron2;

    float posX, posY;
    int level;

    public HexWorld(float height, float sx, float sy, int level) {
        posX = sx;
        posY = sy;
        this.level = level;

        polyhedron0 = new RegularIcosahedron(height);
        polyhedron1 = Polyhedron.uniformTruncate(polyhedron0);
        polyhedron2 = polyhedron1.copy();
        for(int i = 1; i <= level; i++) {
            System.out.println("level: " + i);
            polyhedron2 = Polyhedron.dual(polyhedron2);
            System.out.println("dualed");
            polyhedron2 = Polyhedron.uniformTruncate(polyhedron2);
            System.out.println("truncated");
        }

        polyhedron1.setTransform(new Matrix4().translate(0 , 0, height));
        polyhedron2.setTransform(new Matrix4().translate(0, 0, 2 * height));
    }
}
