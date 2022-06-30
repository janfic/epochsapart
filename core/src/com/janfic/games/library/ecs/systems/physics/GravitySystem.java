package com.janfic.games.library.ecs.systems.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.AccelerationComponent;
import com.janfic.games.library.ecs.components.physics.GravityComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.physics.VelocityComponent;

import java.util.Map;

public class GravitySystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private static final Family family = Family.all(AccelerationComponent.class, GravityComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {
            AccelerationComponent accelerationComponent = Mapper.accelerationComponentMapper.get(entity);
            GravityComponent gravityComponent = Mapper.gravityComponentMapper.get(entity);

            accelerationComponent.acceleration = gravityComponent.gravity;
        }
    }
}
