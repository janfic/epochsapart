package com.janfic.games.library.ecs.components.physics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.function.Consumer;

public class CollideableComponent implements Component {
    public Consumer<Entity> onCollision;
}
