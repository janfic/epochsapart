package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorldTile {

    protected int index;
    public List<WorldTile> neighbors; // clockwise

    public WorldTile(int index) {
        this.index = index;
        this.neighbors = new ArrayList<>();
    }

    public int longitude, latitude;

    public void draw(ShapeRenderer renderer, float x, float y, float rotation, float distance, Set<WorldTile> marked) {
        if(marked.contains(this)) return;
        marked.add(this);
        renderer.circle(x, y,distance/2);
        for (int i = 0; i < neighbors.size(); i++) {
            double theta = 2 * Math.PI * (i+1) / neighbors.size() + Math.PI / 2;
            double nx = Math.cos(theta) * distance + x;
            double ny = Math.sin(theta) * distance + y;

            WorldTile neighbor = neighbors.get(i);
            neighbor.draw(renderer, (float) nx, (float) ny, rotation, distance, marked);
        }
    }

    public void addNeighbor(WorldTile tile) {
        neighbors.add(tile);
    }
}
