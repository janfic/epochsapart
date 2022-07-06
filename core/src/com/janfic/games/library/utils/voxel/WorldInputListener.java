package com.janfic.games.library.utils.voxel;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class WorldInputListener extends ClickListener {

    protected Vector3 worldVoxel;
    protected VoxelWorld world;

    public void setWorldVoxel(Vector3 worldVoxel) {
        this.worldVoxel = worldVoxel;
    }

    public void setWorld(VoxelWorld world) {
        this.world = world;
    }
}
