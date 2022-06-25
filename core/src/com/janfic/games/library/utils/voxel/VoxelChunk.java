package com.janfic.games.library.utils.voxel;

import com.badlogic.gdx.math.Vector3;

public class VoxelChunk {
    public static final int VERTEX_SIZE = 8;
    public static int CHUNK_SIZE_X = 16, CHUNK_SIZE_Y = 16, CHUNK_SIZE_Z = 16;
    public final Vector3 offset = new Vector3();

    public final byte[] voxels;
    public final int topOffset, bottomOffset, leftOffset, rightOffset, frontOffset, backOffset;

    public final int widthTimeHeight;

    public final static int textureSizeWidth = 128 * 3;
    public final static int textureSizeHeight = 128 * 4;

    public VoxelChunk() {
        voxels = new byte[CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_X];
        this.topOffset = CHUNK_SIZE_X * CHUNK_SIZE_Y;
        this.bottomOffset = -CHUNK_SIZE_X * CHUNK_SIZE_Y;
        this.leftOffset = -1;
        this.rightOffset = 1;
        this.frontOffset = -CHUNK_SIZE_X;
        this.backOffset = CHUNK_SIZE_X;
        this.widthTimeHeight = CHUNK_SIZE_X * CHUNK_SIZE_Y;

    }

    public byte getFast(int x, int y, int z) {
        return voxels[x + z * CHUNK_SIZE_X + y * CHUNK_SIZE_X * CHUNK_SIZE_Y];
    }

    public byte get(int x, int y, int z) {
        if (x < 0 || x >= CHUNK_SIZE_X) return 0;
        if (y < 0 || y >= CHUNK_SIZE_Y) return 0;
        if (z < 0 || z >= CHUNK_SIZE_Z) return 0;
        return getFast(x, y, z);
    }

    public void set(int x, int y, int z, byte voxel) {
        if (x < 0 || x >= CHUNK_SIZE_X) return;
        if (y < 0 || y >= CHUNK_SIZE_Y) return;
        if (z < 0 || z >= CHUNK_SIZE_Z) return;
        setFast(x, y, z, voxel);
    }

    public void setFast(int x, int y, int z, byte voxel) {
         voxels[x + z * CHUNK_SIZE_X + y * CHUNK_SIZE_X * CHUNK_SIZE_Y] = voxel;
    }

    public int calculateVertices (float[] vertices) {
        int i = 0;
        int vertexOffset = 0;
        for (int y = 0; y < CHUNK_SIZE_Y; y++) {
            for (int z = 0; z < CHUNK_SIZE_Z; z++) {
                for (int x = 0; x < CHUNK_SIZE_X; x++, i++) {
                    byte voxel = voxels[i];
                    if (voxel == 0) continue;
                    if (y < CHUNK_SIZE_Y - 1) {
                        if (voxels[i + topOffset] == 0) vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    } else {
                        vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    }
                    if (y > 0) {
                        if (voxels[i + bottomOffset] == 0) vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    } else {
                        vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    }
                    if (x > 0) {
                        if (voxels[i + leftOffset] == 0) vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    } else {
                        vertexOffset = createLeft(offset, x, y, z, vertices,vertexOffset, voxel - 1);
                    }
                    if (x < CHUNK_SIZE_X - 1) {
                        if (voxels[i + rightOffset] == 0) vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    } else {
                        vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    }
                    if (z > 0) {
                        if (voxels[i + frontOffset] == 0) vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset,voxel - 1);
                    } else {
                        vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    }
                    if (z < CHUNK_SIZE_Z - 1) {
                        if (voxels[i + backOffset] == 0) vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    } else {
                        vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset, voxel - 1);
                    }
                }
            }
        }
        return vertexOffset / VERTEX_SIZE;
    }

    public static int createTop (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, int texture) {
        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 111f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 64f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 111f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 95f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 80f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 95f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 80f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 64f) / textureSizeHeight;


        return vertexOffset;
    }

    public static int createBottom (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, int texture) {

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 16f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 95f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 47f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 95f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 47f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 64f) / textureSizeHeight;


        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 16f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 64f) / textureSizeHeight;
        return vertexOffset;
    }

    public static int createLeft (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, int texture) {

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 79f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 31f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 79f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 0f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 48f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 0f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 48f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 31f) / textureSizeHeight;

        return vertexOffset;
    }

    public static int createRight (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, int texture) {



        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 48f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 95f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 79f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 95f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 79f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 64f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 48f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 64f) / textureSizeHeight;

        return vertexOffset;
    }

    public static int createFront (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, int texture) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 48f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 32f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 48f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 63f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 79f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 63f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 79f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 32f) / textureSizeHeight;
        return vertexOffset;
    }

    public static int createBack (Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset, int texture) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 48f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 127f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 79f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 127f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 79f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 96f) / textureSizeHeight;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 48f / textureSizeWidth;
        vertices[vertexOffset++] = (128f * texture + 96f) / textureSizeHeight;
        return vertexOffset;
    }
}
