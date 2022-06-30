package com.janfic.games.library.ecs.systems.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.BoundingBoxComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;

public class BoundingBoxSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private final static Family family = Family.all(PositionComponent.class, BoundingBoxComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {
            // Position is center of box
            PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
            BoundingBoxComponent boundingBoxComponent = Mapper.boundingBoxComponentMapper.get(entity);

            float width = boundingBoxComponent.boundingBox.getWidth();
            float height = boundingBoxComponent.boundingBox.getHeight();
            float depth = boundingBoxComponent.boundingBox.getDepth();

            Vector3 size = new Vector3(width / 2, height / 2, depth / 2);
            Vector3 min = positionComponent.position.cpy();
            Vector3 max = positionComponent.position.cpy();

            boundingBoxComponent.boundingBox.set(min.sub(size), max.add(size));
        }
    }
}
