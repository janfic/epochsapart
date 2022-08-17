package com.janfic.games.library.utils.isometric;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.world.DirtyTileComponent;
import com.janfic.games.library.ecs.components.world.TileComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * A voxel chunk structure optimized for rendering in an isometric view.
 */
public class IsometricChunk {

    public static int CHUNK_SIZE_X = 16, CHUNK_SIZE_Y = 16, CHUNK_SIZE_Z = 16;

    public static void setChunkSize(int w) {
        setChunkSize(w,w,w);
    }

    public static void setChunkSize(int x, int y, int z) {
        CHUNK_SIZE_X = x;
        CHUNK_SIZE_Y = y;
        CHUNK_SIZE_Z = z;
    }

    Entity[][][] tiles;
    Vector3 position;
    boolean isDirty;
    boolean isVisible;
    List<Entity> visibleTiles;
    int tileCount = 0;

    public IsometricChunk(int x, int y, int z) {
        tiles = new Entity[CHUNK_SIZE_X][CHUNK_SIZE_Y][CHUNK_SIZE_Z];
        this.position = new Vector3(x * CHUNK_SIZE_X,y * CHUNK_SIZE_Y,z * CHUNK_SIZE_Z);
        isDirty = true;
        isVisible = true;
        visibleTiles = new ArrayList<>();
        tileCount = 0;
    }

    public void set(int x, int y, int z, Entity tile) {
        if(x < 0 || y < 0 || z < 0) return;
        if(x >= CHUNK_SIZE_X || y  >= CHUNK_SIZE_Y || z >= CHUNK_SIZE_Z) return;
        tiles[x][y][z] = tile;
        //TODO: ADD DirtyComponent
        TileComponent tileComponent = Mapper.tileComponentMapper.get(tile);
        tileComponent.isDirty = true;
        tileComponent.isVisible = true;
        tileComponent.chunk = this;
        tile.add(new DirtyTileComponent());
        this.isDirty = true;
        tileCount++;
        //visibleTiles.add(tileComponent);
    }

    public Entity get(int x, int y, int z) {
        if(x < 0 || y < 0 || z < 0) return null;
        if(x >= CHUNK_SIZE_X || y  >= CHUNK_SIZE_Y || z >= CHUNK_SIZE_Z) return null;
        return tiles[x][y][z];
    }

    public TileComponent getTile(int x, int y, int z) {
        Entity tile = get(x,y,z);
        if(tile == null) return null;
        return Mapper.tileComponentMapper.get(get(x, y, z));
    }

    private boolean isTileRendering(int x, int y, int z) {
        TileComponent tileComponent = getTile(x,y,z);
        return tileComponent != null;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
        for (Entity e : visibleTiles) {
            TileComponent visibleTile = Mapper.tileComponentMapper.get(e);
            visibleTile.isVisible = isVisible;
            //TODO: ADD DirtyComponent
            e.add(new DirtyTileComponent());
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean hasVisibleTiles() {
        return visibleTiles.size() > 0;
    }

    public final int maxTiles = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z;

    public boolean isOpaque() {
        return tileCount > maxTiles / 4;
    }

    public int clean() {
        visibleTiles.clear();
        for (int y = tiles[0].length - 1; y >= 0 ; y--) {
            for (int z = 0; z < tiles[0][y].length; z++) {
                for (int x = 0; x < tiles.length; x++) {
                    Entity e = get(x,y,z);
                    TileComponent tileComponent = getTile( x, y, z);
                    if(tileComponent == null) continue;
                    if(isTileRendering(x,y + 1, z) && isTileRendering(x, y, z - 1) && isTileRendering(x - 1, y, z)) {
                        tileComponent.isVisible = false;
                    }
                    else {
                        tileComponent.isVisible = true;
                        visibleTiles.add(e);
                    }
                    //TODO: ADD DirtyComponent
                    //e.add(new DirtyTileComponent());
                }
            }
        }
        this.isDirty = false;
        return visibleTiles.size();
    }

}