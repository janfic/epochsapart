package com.janfic.games.library.ecs.systems.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.AccelerationComponent;
import com.janfic.games.library.ecs.components.physics.ForceComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.physics.VelocityComponent;

public class PhysicsSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private Family family = Family.all(PositionComponent.class, VelocityComponent.class, AccelerationComponent.class, ForceComponent.class).get();

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {

            PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
            VelocityComponent velocityComponent = Mapper.velocityComponentMapper.get(entity);
            AccelerationComponent accelerationComponent = Mapper.accelerationComponentMapper.get(entity);
            ForceComponent forceComponent = Mapper.forceComponentMapper.get(entity);

            Vector3 acceleration = new Vector3();
            for (Vector3 force : forceComponent.forces) {
                acceleration.add(force);
            }

            accelerationComponent.acceleration = acceleration;
            velocityComponent.velocity.add(accelerationComponent.acceleration.cpy().scl(deltaTime));
            positionComponent.position.add(velocityComponent.velocity.cpy());
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
