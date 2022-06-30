package com.janfic.games.library.ecs.systems.rendering;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.CameraFollowComponent;

public class CameraFollowSystem extends EntitySystem {

    private ImmutableArray<Entity> followEntities, cameras;

    private final static Family followFamily = Family.all(CameraFollowComponent.class, PositionComponent.class).get();
    private final static Family camerasFamily = Family.all(CameraFollowComponent.class, CameraComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        followEntities = engine.getEntitiesFor(followFamily);
        cameras = engine.getEntitiesFor(camerasFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity camera : cameras) {
            CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(camera);

            for (Entity followEntity : followEntities) {
                PositionComponent positionComponent = Mapper.positionComponentMapper.get(followEntity);
                cameraComponent.camera.lookAt(positionComponent.position);
                cameraComponent.camera.up.set(Vector3.Y);
                cameraComponent.camera.update();
            }
        }
    }
}
