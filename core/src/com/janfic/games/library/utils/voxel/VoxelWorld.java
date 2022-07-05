package com.janfic.games.library.utils.voxel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
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

    public int[][] maxHeights;

    public TextureAtlas atlas;

    public VoxelWorld(FileHandle tilesDirectory, int chunksX, int chunksY, int chunksZ) {
        this.chunks = new VoxelChunk[chunksX * chunksY * chunksZ];
        this.meshes = new Mesh[chunksX * chunksY * chunksZ];
        this.dirty = new boolean[chunksX * chunksY * chunksZ];
        this.numVertices = new int[chunksX * chunksY * chunksZ];
        this.materials = new Material[tilesDirectory.list(".png").length];
        this.vertices = new float[VoxelChunk.VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
        this.voxelsX = chunksX * CHUNK_SIZE_X;
        this.voxelsY = chunksX * CHUNK_SIZE_Y;
        this.voxelsZ = chunksX * CHUNK_SIZE_Z;
        this.chunksX = chunksX;
        this.chunksY = chunksY;
        this.chunksZ = chunksZ;
        this.maxHeights = new int[voxelsX][voxelsZ];

        TexturePacker.Settings s  = new TexturePacker.Settings();
        s.edgePadding = false;
        s.paddingX = 0;
        s.paddingY = 0;
        TexturePacker.process(s, "models/tileTextures/test","models/tileTextures/output/","pack");
        this.atlas = new TextureAtlas(Gdx.files.local("models/tileTextures/output/pack.atlas"));

        // Make Chunks
        int i = 0;
        for (int y = 0; y < chunksY; y++) {
            for (int z = 0; z < chunksZ; z++) {
                for (int x = 0; x < chunksX; x++) {
                    VoxelChunk chunk = new VoxelChunk(atlas);
                    chunk.offset.set(x * CHUNK_SIZE_X, y * CHUNK_SIZE_Y, z * CHUNK_SIZE_Z);
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
            materials[i] = new Material(TextureAttribute.createDiffuse(atlas.getTextures().first()));
        }
    }

    public CubeVoxel get(float x, float y, float z) {
        int ix = (int)x;
        int iy = (int)y;
        int iz = (int)z;
        int chunkX = ix / CHUNK_SIZE_X;
        if (chunkX < 0 || chunkX >= chunksX) return null;
        int chunkY = iy / CHUNK_SIZE_Y;
        if (chunkY < 0 || chunkY >= chunksY) return null;
        int chunkZ = iz / CHUNK_SIZE_Z;
        if (chunkZ < 0 || chunkZ >= chunksZ) return null;
        return chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z);
    }

    public void set(float x, float y, float z, CubeVoxel voxel) {
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
        if(ix < 0 || ix >= voxelsX) return;
        if(iz < 0 || iz  >= voxelsZ) return;
        if(maxHeights[ix][iz ] < iy && voxel != null) maxHeights[ix][iz ] = iy;
    }

    public int getMaxHeight(int x, int z) {
        return maxHeights[x][z];
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

    public Vector3 getChunk(Ray ray) {

        float dist = 2000;

        Vector3 traverse = new Vector3();

        traverse.x = (float) Math.floor(ray.origin.x);
        traverse.y = (float) Math.floor(ray.origin.y);
        traverse.z = (float) Math.floor(ray.origin.z);

        Vector3 step = new Vector3();
        Vector3 tMax = new Vector3();
        Vector3 delta = new Vector3();

        float dx = ray.direction.x;
        float dy = ray.direction.y;
        float dz = ray.direction.z;

        step.x = Math.signum(dx);
        step.y = Math.signum(dy);
        step.z = Math.signum(dz);

        tMax.x = intbound(ray.origin.x, dx);
        tMax.y = intbound(ray.origin.y, dy);
        tMax.z = intbound(ray.origin.z, dz);

        delta.x = step.x / dx;
        delta.y = step.y / dy;
        delta.z = step.z / dz;

        if(dx == 0 && dy == 0 && dz == 0) return null;


        while(!inBounds(traverse)) {
            traverse(traverse, step, tMax, delta);
            if(traverse.dst(ray.origin) >= dist) return null;
        }

        while(get(traverse.x, traverse.y, traverse.z) == null) {
            traverse(traverse, step, tMax, delta);
            if(traverse.dst(ray.origin) >= dist) return null;
        }
        return traverse;
    }

    private boolean inBounds(Vector3 vector) {
        return !(vector.x < 0 || vector.x >= voxelsX || vector.y < 0 || vector.y >= voxelsY || vector.z < 0 || vector.z >= voxelsZ);
    }

    private void traverse(Vector3 traverse, Vector3 step, Vector3 tMax, Vector3 delta) {
        if(tMax.x < tMax.y) {
            if(tMax.x < tMax.z) {
                tMax.x += delta.x;
                traverse.x += step.x;
            }
            else {
                tMax.z += delta.z;
                traverse.z += step.z;
            }
        }
        else {
            if(tMax.y < tMax.z) {
                tMax.y += delta.y;
                traverse.y += step.y;
            }
            else {
                tMax.z += delta.z;
                traverse.z += step.z;
            }
        }
    }

    private float intbound(float s, float ds) {
        if(ds < 0) {
            return intbound(-s, -ds);
        }
        else {
            s = mod(s, 1);
            return (1-s)/ds;
        }
    }

    private float mod(float value, float modulus) {
        return (value % modulus + modulus) % modulus;
    }
}
