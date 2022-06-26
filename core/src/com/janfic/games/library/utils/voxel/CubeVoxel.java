package com.janfic.games.library.utils.voxel;

import com.badlogic.gdx.math.Vector3;

public class CubeVoxel extends Voxel {

    // Top, Bottom, Left, Right, Front, Back

    private final static float[] vertices = new float[] {
            1, 1, 1, 1, 1, 0, 0, 1, 0, 0, 1, 1,
            0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 0, 1,
            0, 1, 1, 0, 1, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1,
            0, 0, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1,
            0, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 0
    },
    uvs = new float[] {
            111f, 64, 111, 95, 80, 95, 80, 64,
            16, 95, 47, 95, 47, 64, 16, 64,
            79, 31, 79, 0, 48, 0, 48, 31,
            48, 95, 79, 95, 79, 64, 48, 64,
            48, 32, 48, 63, 79, 63, 79, 32,
            48, 127, 79, 127, 79, 96, 48, 96
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
    public int create(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, boolean[] neighbors, byte voxel) {
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
                    vertices[vertexOffset++] = uvs[k] / VoxelChunk.textureSizeWidth;
                    vertices[vertexOffset++] = (128 * voxel + uvs[k + 1]) / VoxelChunk.textureSizeHeight;
                    k += 2;
                }
            }
        }
        return vertexOffset;
    }
}
