package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.math.Matrix4;
import com.github.czyzby.noise4j.map.generator.noise.NoiseGenerator;

public class HexWorld {

    public Polyhedron polyhedron;

    float posX, posY;
    int level;
    public float height;

    public HexWorld(float height, float sx, float sy, int level) {
        posX = sx;
        posY = sy;
        this.height = height;
        this.level = level;
        polyhedron = new RegularIcosahedron(height);

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

    public void generateTerrain() {
        for (Face face : polyhedron.faces) {
            face.setHeight((float) Math.random());
        }
    }
}
