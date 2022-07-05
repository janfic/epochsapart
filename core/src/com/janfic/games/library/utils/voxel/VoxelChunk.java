package com.janfic.games.library.utils.voxel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import org.w3c.dom.Text;

public class VoxelChunk {
    public static final int VERTEX_SIZE = 8;
    public static int CHUNK_SIZE_X = 16, CHUNK_SIZE_Y = 16, CHUNK_SIZE_Z = 16;
    public final Vector3 offset = new Vector3();

    public final CubeVoxel[] voxels;
    public final int topOffset, bottomOffset, leftOffset, rightOffset, frontOffset, backOffset;

    public final int widthTimeHeight;

    public final static int textureSizeWidth = 128 * 3;
    public final static int textureSizeHeight = 128 * 5;

    TextureAtlas atlas;
    CubeVoxel cubeVoxel;
    public VoxelChunk(TextureAtlas atlas) {
        cubeVoxel = new CubeVoxel(atlas, "dirt");
        voxels = new CubeVoxel[CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_X];
        this.topOffset = CHUNK_SIZE_X * CHUNK_SIZE_Y;
        this.bottomOffset = -CHUNK_SIZE_X * CHUNK_SIZE_Y;
        this.leftOffset = -1;
        this.rightOffset = 1;
        this.frontOffset = -CHUNK_SIZE_X;
        this.backOffset = CHUNK_SIZE_X;
        this.widthTimeHeight = CHUNK_SIZE_X * CHUNK_SIZE_Z;
    }

    public CubeVoxel getFast(int x, int y, int z) {
        return voxels[x + z * CHUNK_SIZE_X + y * widthTimeHeight];
    }

    public CubeVoxel get(int x, int y, int z) {
        if (x < 0 || x >= CHUNK_SIZE_X) return null;
        if (y < 0 || y >= CHUNK_SIZE_Y) return null;
        if (z < 0 || z >= CHUNK_SIZE_Z) return null;
        return getFast(x, y, z);
    }

    public void set(int x, int y, int z, CubeVoxel voxel) {
        if (x < 0 || x >= CHUNK_SIZE_X) return;
        if (y < 0 || y >= CHUNK_SIZE_Y) return;
        if (z < 0 || z >= CHUNK_SIZE_Z) return;
        setFast(x, y, z, voxel);
    }

    public void setFast(int x, int y, int z, CubeVoxel voxel) {
         voxels[x + z * CHUNK_SIZE_X + y * widthTimeHeight] = voxel;
    }

    boolean[] neighbors = new boolean[6];
    public int calculateVertices (float[] vertices) {
        int i = 0;
        int vertexOffset = 0;
        for (int y = 0; y < CHUNK_SIZE_Y; y++) {
            for (int z = 0; z < CHUNK_SIZE_Z; z++) {
                for (int x = 0; x < CHUNK_SIZE_X; x++, i++) {
                    CubeVoxel voxel = voxels[i];
                    if (voxel == null) continue;
                    neighbors[0] = y >= CHUNK_SIZE_Y - 1 || (voxels[i + topOffset] == null);
                    neighbors[1] = y <= 0 || (voxels[i + bottomOffset] == null);
                    neighbors[2] = x >= 0 || (voxels[i + leftOffset] == null);
                    neighbors[3] = x >= CHUNK_SIZE_X - 1 || (voxels[i + rightOffset] == null);
                    neighbors[4] = z >= CHUNK_SIZE_Z - 1 || (voxels[i + backOffset] == null);
                    neighbors[5] = z <= 0 || (voxels[i + frontOffset] == null);
                    vertexOffset = voxel.create(offset, x, y, z, vertices, vertexOffset, neighbors);
                }
            }
        }
        return vertexOffset / VERTEX_SIZE;
    }
}
