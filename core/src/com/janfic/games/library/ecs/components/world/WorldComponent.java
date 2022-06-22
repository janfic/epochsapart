package com.janfic.games.library.ecs.components.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

public class WorldComponent implements Component {
    public int centerX, centerY, centerZ;
    public Entity[][][] world;
}
