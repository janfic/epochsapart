package com.janfic.games.library.utils.voxel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public abstract class VoxelTexture {
    private Texture texture;
    private float[] uvs;

    public VoxelTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return texture;
    }

}
