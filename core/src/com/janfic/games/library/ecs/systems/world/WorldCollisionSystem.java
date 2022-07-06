package com.janfic.games.library.ecs.systems.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.*;
import com.janfic.games.library.ecs.components.world.WorldComponent;

public class WorldCollisionSystem extends EntitySystem {
    private ImmutableArray<Entity> entities, worldEntity;

    private final static Family entityFamily = Family.all(PositionComponent.class, BoundingBoxComponent.class, VelocityComponent.class, AccelerationComponent.class).get(),
            worldFamily = Family.all(WorldComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(entityFamily);
        worldEntity = engine.getEntitiesFor(worldFamily);
    }

    Vector3 antiGravity = new Vector3(0,1,0);

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(worldEntity.size() == 0) return;
        WorldComponent worldComponent = Mapper.worldComponentMapper.get(worldEntity.first());

        for (Entity entity : entities) {
            PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
            VelocityComponent velocityComponent = Mapper.velocityComponentMapper.get(entity);
            ForceComponent forceComponent = Mapper.forceComponentMapper.get(entity);
            BoundingBoxComponent boundingBoxComponent = Mapper.boundingBoxComponentMapper.get(entity);

            if(!(positionComponent.position.x < worldComponent.world.voxelsX
                    && positionComponent.position.x >= 0
                    && positionComponent.position.z >= 0
                    && positionComponent.position.z <= worldComponent.world.voxelsZ)) continue;

            int height = worldComponent.world.getMaxHeight((int)(positionComponent.position.x), (int)(positionComponent.position.z));

            if(positionComponent.position.y + velocityComponent.velocity.y - boundingBoxComponent.boundingBox.getHeight() / 2f < height + 1
                || positionComponent.position.y == height + boundingBoxComponent.boundingBox.getHeight() / 2f + 1
            ) {
                positionComponent.position.y = height + boundingBoxComponent.boundingBox.getHeight() / 2f + 1;
                velocityComponent.velocity.y = 0;
                if (!forceComponent.forces.contains(antiGravity)) {
                    forceComponent.forces.add(antiGravity);
                }
            }
            else {
                forceComponent.forces.remove(antiGravity);
            }
        }
    }
}
