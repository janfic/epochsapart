package com.janfic.games.library.ecs.systems.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.BoundingBoxComponent;
import com.janfic.games.library.ecs.components.physics.CollideableComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;

/**
 *
 */
public class CollisionSystem extends EntitySystem {

    private ImmutableArray<Entity> collidable, colliders;

    private static final Family collideableFamily = Family.all(PositionComponent.class, BoundingBoxComponent.class, CollideableComponent.class).get();
    private static final Family colliderFamily = Family.all(PositionComponent.class, BoundingBoxComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        collidable = engine.getEntitiesFor(collideableFamily);
        colliders = engine.getEntitiesFor(colliderFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : collidable) {
            PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
            BoundingBoxComponent boundingBoxComponent = Mapper.boundingBoxComponentMapper.get(entity);
            CollideableComponent collideableComponent = Mapper.collideableComponentMapper.get(entity);

            float range = boundingBoxComponent.boundingBox.max.dst2(boundingBoxComponent.boundingBox.min);

            for (Entity collider : colliders) {
                PositionComponent colliderPosition = Mapper.positionComponentMapper.get(collider);
                BoundingBoxComponent colliderBox = Mapper.boundingBoxComponentMapper.get(collider);

                float size = colliderBox.boundingBox.max.dst2(colliderBox.boundingBox.min);
                float distance = colliderPosition.position.dst2(positionComponent.position);

                if(distance < size + range) {
                    if(boundingBoxComponent.boundingBox.contains(colliderBox.boundingBox) || boundingBoxComponent.boundingBox.intersects(colliderBox.boundingBox)) {
                        collideableComponent.onCollision.accept(collider);
                    }
                }
            }
        }
    }
}
