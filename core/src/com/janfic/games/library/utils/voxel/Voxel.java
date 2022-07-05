package com.janfic.games.library.utils.voxel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

public abstract class Voxel {

    protected String type;

    public Voxel(String type) {
        this.type = type;
    }

    /**
     *
     * @param offset
     * @param x
     * @param y
     * @param z
     * @param vertices
     * @param vertexOffset
     * @param neighbors - a length 6 boolean array representing whether neighboring voxels exist.
     * @return
     */
    public abstract int create(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, boolean[] neighbors);

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
