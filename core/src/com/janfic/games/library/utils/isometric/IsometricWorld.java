package com.janfic.games.library.utils.isometric;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.rendering.WorldToScreenTransformComponent;
import com.janfic.games.library.ecs.components.world.TileComponent;


public class IsometricWorld {

    public int tilesWidth, tilesHeight, tilesDepth;
    public int chunkWidth, chunkHeight, chunkDepth;

    private final IsometricChunk[][][] chunks;
    private int[][] heightMap;

    public IsometricWorld(int worldWidth, int worldHeight, int worldDepth) {
        this.tilesWidth = worldWidth;
        this.tilesHeight = worldHeight;
        this.tilesDepth = worldDepth;
        this.chunkWidth = (int) Math.ceil((float)worldWidth / IsometricChunk.CHUNK_SIZE_X);
        this.chunkHeight = (int) Math.ceil((float)worldHeight / IsometricChunk.CHUNK_SIZE_Y);
        this.chunkDepth = (int) Math.ceil((float)worldDepth / IsometricChunk.CHUNK_SIZE_Z);

        this.chunks = new IsometricChunk[chunkWidth][chunkHeight][chunkDepth];
        for (int x = 0; x < chunkWidth; x++) {
            for (int y = 0; y < chunkHeight; y++) {
                for (int z = 0; z < chunkDepth; z++) {
                    this.chunks[x][y][z] = new IsometricChunk(x,y,z);
                }
            }
        }
        this.heightMap = new int[worldWidth][worldDepth];
    }

    public void set(int x, int y, int z, Entity tile) {
        if(x >= tilesWidth || y >= tilesHeight || z >= tilesDepth) return;
        if(x < 0 || y < 0 || z < 0) return;
        int chunkX = x / IsometricChunk.CHUNK_SIZE_X;
        int chunkY = y / IsometricChunk.CHUNK_SIZE_Y;
        int chunkZ = z / IsometricChunk.CHUNK_SIZE_Z;
        chunks[chunkX][chunkY][chunkZ].set(
                x - chunkX * IsometricChunk.CHUNK_SIZE_X,
                y - chunkY * IsometricChunk.CHUNK_SIZE_Y,
                z - chunkZ * IsometricChunk.CHUNK_SIZE_Z, tile);
        if(heightMap[x][z] < y) heightMap[x][z] = y;
    }

    public Entity get(int x, int y, int z) {
        if(x >= tilesWidth || y >= tilesHeight || z >= tilesDepth) return null;
        if(x < 0 || y < 0 || z < 0) return null;
        int chunkX = x / IsometricChunk.CHUNK_SIZE_X;
        int chunkY = y / IsometricChunk.CHUNK_SIZE_Y;
        int chunkZ = z / IsometricChunk.CHUNK_SIZE_Z;
        return chunks[chunkX][chunkY][chunkZ].get(
                x - chunkX * IsometricChunk.CHUNK_SIZE_X,
                y - chunkY * IsometricChunk.CHUNK_SIZE_Y,
                z - chunkZ * IsometricChunk.CHUNK_SIZE_Z);
    }

    Rectangle rectangle = new Rectangle();
    Vector3 position = new Vector3();
    Vector3 out = new Vector3();

    public void updateRendering(OrthographicCamera camera, WorldToScreenTransformComponent.WorldToScreenTransform transform) {

        rectangle.setSize(camera.viewportWidth * 3, camera.viewportHeight * 3);
        rectangle.setPosition(camera.position.x - rectangle.width / 2, camera.position.y - rectangle.height / 2);

        int cCount = 0;
        for (int y = chunks[0].length - 1; y >= 0; y--) {
            for (int x = 0; x < chunks.length; x++) {
                for (int z = 0; z < chunks[x][y].length; z++) {
                    if(chunks[x][y][z].isDirty)
                        System.out.println("tiles: " + chunks[x][y][z].clean());

                    position.set((x + 0.5f) * IsometricChunk.CHUNK_SIZE_X, (y + 0.5f) * IsometricChunk.CHUNK_SIZE_Y, (z + 0.5f) * IsometricChunk.CHUNK_SIZE_Z);
                    out = transform.worldToScreen(position, out);
                    if(!chunks[x][y][z].hasVisibleTiles()) {
                        setChunkVisible(x,y,z,false);
                        continue;
                    }
                    boolean covered = isChunkRendering(x,y + 1, z) && isChunkRendering(x - 1, y, z) && isChunkRendering(x, y, z - 1);
                    if(!covered) {
                        if(rectangle.contains(out.x, out.y)) {
                            setChunkVisible(x, y, z, true);
                            cCount++;
                        }
                        else {
                            setChunkVisible(x, y, z, false);
                        }
                    }
                    else {
                        setChunkVisible(x,y,z,false);
                    }
                }
            }
        }
        System.out.println("renderedChunkCount: " + cCount);
    }

    private void setChunkVisible(int x, int y, int z, boolean visible) {
        if(chunks[x][y][z].isVisible() != visible)
            chunks[x][y][z].setVisible(visible);
    }

    public boolean isChunkRendering(int x, int y, int z) {
        if(x < 0 || y < 0 || z < 0) return false;
        if(x >= chunkWidth || y >= chunkHeight || z >= chunkDepth) return false;
        return chunks[x][y][z].isOpaque();
    }

    public int getHeight(int x, int z) {
        return heightMap[x][z];
    }

    public boolean isTileAt(int x, int y, int z) {
        Entity e = get(x,y,z);
        TileComponent tileComponent = e == null ? null : Mapper.tileComponentMapper.get(e);
        return tileComponent != null;
    }
}
