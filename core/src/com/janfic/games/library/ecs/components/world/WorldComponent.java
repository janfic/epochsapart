package com.janfic.games.library.ecs.components.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.janfic.games.library.utils.voxel.VoxelWorld;

public class WorldComponent implements Component {
    public int centerX, centerY, centerZ;
    public VoxelWorld world;
}
