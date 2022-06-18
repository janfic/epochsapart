package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.CameraComponent;
import com.janfic.games.library.ecs.components.PositionComponent;

public class CameraPositionSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private static final Family cameraFamily = Family.all(PositionComponent.class, CameraComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(cameraFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {
            PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
            CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(entity);

            cameraComponent.camera.position.set(positionComponent.position);
            cameraComponent.camera.update();
        }
    }
}
