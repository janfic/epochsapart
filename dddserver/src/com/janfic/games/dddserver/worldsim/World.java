package com.janfic.games.dddserver.worldsim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class World {
    public int size; // 6, 10, 27, 48
    public List<List<WorldTile>> layers;
    public int tileCount;

    int[] layerCount = new int[]{5,10,15,15,15,15,10,5};
    int[][] layerMake = new int[][] {
            {1},
            {1},
            {0,1,1},
            {1},
            {1},
            {0,1,1},
            {1},
            {1}
    };

    public World(int size) {
        layers = new ArrayList<>();

        // base
        List<WorldTile> layer1 = new ArrayList<>();
        PentTile pole = new PentTile(tileCount++);
        layer1.add(pole);
        layers.add(layer1);

        for (int f = 0; f < layerCount.length; f++) {
            int count = layerCount[f];
            int[] make = layerMake[f];
            List<WorldTile> layer = new ArrayList<>();
            for (int i = 0, m = 0; i < count; i++, m = (m + 1) % make.length) {
                layer.add(make[m] == 0 ? new PentTile(i) : new HexTile(i));
            }
            layers.add(layer);
        }

        List<WorldTile> bottom = new ArrayList<>();
        PentTile p = new PentTile(0);
        bottom.add(p);
        layers.add(bottom);


        // neighbors
        List<WorldTile> first = layers.get(1);
        for (int i = 0; i < first.size(); i++) {
            WorldTile worldTile = first.get(i);
            pole.addNeighbor(worldTile);
        }

        for (List<WorldTile> layer : layers) {
            for (WorldTile worldTile : layer) {
                
            }
        }
        
    }


    public void draw(ShapeRenderer renderer) {
        WorldTile tile = layers.get(0).get(0);
        tile.draw(renderer, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 0, 50, new HashSet<>());
    }

    @Override
    public String toString() {
        StringBuilder r = new StringBuilder();
        for (List<WorldTile> layer : layers) {
            for (WorldTile worldTile : layer) {
                r.append("\t" + worldTile.toString());
            }
            r.append("\n");
        }
        return r.toString();
    }

    public WorldTile getTileByCoord(int layer, float theta) {
        List<WorldTile> tiles = layers.get(layer);
        int index = (int) (theta * tiles.size()) % tiles.size();
        return tiles.get(index);
    }
}
