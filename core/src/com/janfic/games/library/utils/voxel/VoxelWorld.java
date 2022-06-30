package com.janfic.games.library.utils.voxel;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import static com.janfic.games.library.utils.voxel.VoxelChunk.*;

public class VoxelWorld implements RenderableProvider {

    public final VoxelChunk[] chunks;
    public final Mesh[] meshes;
    public final Material[] materials;
    public final float[] vertices;
    public final boolean[] dirty;
    public final int[] numVertices;

    public final int voxelsX, voxelsY, voxelsZ;
    public final int chunksX, chunksY, chunksZ;

    public final Texture[] tiles;

    public VoxelWorld(Texture[] tiles, int chunksX, int chunksY, int chunksZ) {
        this.chunks = new VoxelChunk[chunksX * chunksY * chunksZ];
        this.meshes = new Mesh[chunksX * chunksY * chunksZ];
        this.dirty = new boolean[chunksX * chunksY * chunksZ];
        this.numVertices = new int[chunksX * chunksY * chunksZ];
        this.materials = new Material[tiles.length];
        this.vertices = new float[VoxelChunk.VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
        this.voxelsX = chunksX * CHUNK_SIZE_X;
        this.voxelsY = chunksX * CHUNK_SIZE_Y;
        this.voxelsZ = chunksX * CHUNK_SIZE_Z;
        this.chunksX = chunksX;
        this.chunksY = chunksX;
        this.chunksZ = chunksX;
        this.tiles = tiles;

        // Make Chunks
        int i = 0;
        for (int y = 0; y < chunksY; y++) {
            for (int z = 0; z < chunksZ; z++) {
                for (int x = 0; x < chunksX; x++) {
                    VoxelChunk chunk = new VoxelChunk();
                    chunk.offset.set((x - chunksX / 2f) * CHUNK_SIZE_X, y * CHUNK_SIZE_Y, (z - chunksZ / 2f) * CHUNK_SIZE_Z);
                    chunks[i++] = chunk;
                }
            }
        }

        // Make Meshes
        int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 6 / 3;
        short[] indices =  new short[len];
        short j = 0;
        for (i = 0;  i < len; i+= 6, j += 4) {
            indices[i + 0] = (short)(j + 0);
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = (short)(j + 0);
        }
        for (i = 0; i < meshes.length; i++) {
            meshes[i] = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 4,
                    CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 36 / 3,
                    VertexAttribute.Position(),
                    VertexAttribute.Normal(),
                    VertexAttribute.TexCoords(0)
                    );
            meshes[i].setIndices(indices);
        }

        // Dirty
        for (i = 0; i < dirty.length; i++) {
            dirty[i] = true;
        }

        // numVerts
        for (i = 0; i < numVertices.length; i++) {
            numVertices[i] = 0;
        }

        for (i = 0; i < materials.length; i++) {
            materials[i] = new Material(TextureAttribute.createDiffuse(tiles[i]));
        }
    }

    public byte get(float x, float y, float z) {
        int ix = (int)x;
        int iy = (int)y;
        int iz = (int)z;
        int chunkX = ix / CHUNK_SIZE_X;
        if (chunkX < 0 || chunkX >= chunksX) return 0;
        int chunkY = iy / CHUNK_SIZE_Y;
        if (chunkY < 0 || chunkY >= chunksY) return 0;
        int chunkZ = iz / CHUNK_SIZE_Z;
        if (chunkZ < 0 || chunkZ >= chunksZ) return 0;
        return chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz * CHUNK_SIZE_Z);
    }

    public void set(float x, float y, float z, byte voxel) {
        int ix = (int)x;
        int iy = (int)y;
        int iz = (int)z;
        int chunkX = ix / CHUNK_SIZE_X;
        if (chunkX < 0 || chunkX >= chunksX) return;
        int chunkY = iy / CHUNK_SIZE_Y;
        if (chunkY < 0 || chunkY >= chunksY) return;
        int chunkZ = iz / CHUNK_SIZE_Z;
        if (chunkZ < 0 || chunkZ >= chunksZ) return;
        chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z, voxel);
        dirty[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ] = true;
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        int renderedChunks = 0;
        for (int i = 0; i < chunks.length; i++) {
            VoxelChunk chunk = chunks[i];
            Mesh mesh = meshes[i];
            if(dirty[i]) {
                int numVerts = chunk.calculateVertices(vertices);
                numVertices[i] = numVerts / 4 * 6;
                mesh.setVertices(vertices, 0, numVerts * VoxelChunk.VERTEX_SIZE);
                dirty[i] = false;
            }
            if(numVertices[i] == 0) continue;;
            Renderable renderable = pool.obtain();
            renderable.material = materials[0];
            renderable.meshPart.mesh = mesh;
            renderable.meshPart.offset = 0;
            renderable.meshPart.size = numVertices[i];
            renderable.meshPart.primitiveType = GL30.GL_TRIANGLES;
            renderable.worldTransform.setToTranslation(Vector3.Zero);
            renderables.add(renderable);
            renderedChunks++;
        }
    }
}
