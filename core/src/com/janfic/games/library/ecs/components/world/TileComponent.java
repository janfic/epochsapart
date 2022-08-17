package com.janfic.games.library.ecs.components.world;

import com.badlogic.ashley.core.Component;
import com.janfic.games.library.utils.isometric.IsometricChunk;

public class TileComponent implements Component {
    public String name;
    public IsometricChunk chunk;
    public boolean isVisible, isDirty;
}