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
        polyhedron1 = Polyhedron.uniformTruncate(polyhedron0);
        polyhedron2 = polyhedron1.copy();
        for(int i = 1; i <= level; i++) {
            System.out.println("level: " + i);
            System.out.print("dual...");
            polyhedron2 = Polyhedron.dual(polyhedron2);
            System.out.println("done");
            System.out.print("truncate...");
            polyhedron2 = Polyhedron.uniformTruncate(polyhedron2);
            System.out.println("done");
        }
//        polyhedron = Polyhedron.sphereProject(polyhedron2, height / 2);
        polyhedron = polyhedron2;
    }
}
