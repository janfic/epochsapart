package com.janfic.games.dddserver.worldsim;

public class HexWorld {

    public Polyhedron polyhedron;

    float posX, posY;
    int level;

    public HexWorld(float height, float sx, float sy, int level) {
        posX = sx;
        posY = sy;
        this.level = level;

        polyhedron = new RegularIcosahedron(height);

        for (Face face : polyhedron.getFaces()) {
            polyhedron.vertices.add(face.center);
            for (Face neighbor : face.neighbors) {
                Edge ne = new Edge(face.center, neighbor.center);
                if(polyhedron.edges.contains(ne)) continue;
                polyhedron.edges.add(ne);
            }
        }

    }
}
