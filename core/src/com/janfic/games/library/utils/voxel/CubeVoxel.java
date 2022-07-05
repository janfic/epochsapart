package com.janfic.games.library.utils.voxel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;

public class CubeVoxel extends Voxel {

    TextureAtlas atlas;
    TextureRegion region;

    public CubeVoxel(TextureAtlas atlas, String type) {
        super(type);
        this.atlas = atlas;
        this.region = this.atlas.findRegion(type);
    }

    // Rows = {Top, Bottom, Left, Right, Front, Back}
    private final static float[] vertices = new float[] {
            1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1,
            0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1,
            0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1,
            0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
            0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0
    },
    uvs = new float[] {
            32, 0, 32, 32, 0, 32, 0, 0,
            0, 32, 32, 32, 32, 0, 0, 0,
            32, 0, 32, 32, 0, 32, 0, 0,
            0, 32, 32, 32, 32, 0, 0, 0,
            0, 0, 0, 32, 32, 32, 32, 0,
            0, 32, 32, 32, 32, 0, 0, 0
    },

    normals = new float[] {
            0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
            0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0,
            -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,
            1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
            0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
            0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,
    };

    @Override
    public int create(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, boolean[] neighbors) {
        // Optimize for no multiplication
        for (int i = 0; i < neighbors.length; i++) {
            if(neighbors[i]) {
                int k = i * 8;
                for(int j = 0; j < 12; j += 3) {
                    int o = i * 12 + j;
                    vertices[vertexOffset++] = offset.x + x + CubeVoxel.vertices[o];
                    vertices[vertexOffset++] = offset.y + y + CubeVoxel.vertices[o + 1];
                    vertices[vertexOffset++] = offset.z + z + CubeVoxel.vertices[o + 2];
                    vertices[vertexOffset++] = normals[o];
                    vertices[vertexOffset++] = normals[o + 1];
                    vertices[vertexOffset++] = normals[o + 2];
                    vertices[vertexOffset++] = this.region.getU() + (uvs[k] + (i * 32))/ this.region.getTexture().getWidth();
                    vertices[vertexOffset++] = this.region.getV() + (uvs[k + 1]) / this.region.getTexture().getHeight();
                    k += 2;
                }
            }
        }
        return vertexOffset;
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void setType(String type) {
        super.setType(type);
        this.region = this.atlas.findRegion(type);
    }

    public TextureRegion getRegion() {
        return region;
    }
}
