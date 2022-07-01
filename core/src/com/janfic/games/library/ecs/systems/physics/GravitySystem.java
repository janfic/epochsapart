package com.janfic.games.library.ecs.systems.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.*;

import java.util.Map;

public class GravitySystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private static final Family family = Family.all(ForceComponent.class, GravityComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {
            ForceComponent forceComponent = Mapper.forceComponentMapper.get(entity);
            GravityComponent gravityComponent = Mapper.gravityComponentMapper.get(entity);

            if(!forceComponent.forces.contains(gravityComponent.gravity)) {
                forceComponent.forces.add(gravityComponent.gravity);
            }
        }
    }
}
