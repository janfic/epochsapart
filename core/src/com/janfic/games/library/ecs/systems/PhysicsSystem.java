package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.AccelerationComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.physics.VelocityComponent;

public class PhysicsSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private Family family = Family.all(PositionComponent.class, VelocityComponent.class, AccelerationComponent.class).get();

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {
            PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
            VelocityComponent velocityComponent = Mapper.velocityComponentMapper.get(entity);
            AccelerationComponent accelerationComponent = Mapper.accelerationComponentMapper.get(entity);

            velocityComponent.velocity.add(accelerationComponent.acceleration.cpy().scl(deltaTime));
            positionComponent.position.add(velocityComponent.velocity.cpy().scl(deltaTime));
        }
    }
    

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
        entities = null;
    }
}
