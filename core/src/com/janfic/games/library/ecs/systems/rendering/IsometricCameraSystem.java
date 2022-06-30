package com.janfic.games.library.ecs.systems.rendering;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.isometric.IsometricCameraComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.CameraFollowComponent;

import java.util.Map;

public class IsometricCameraSystem extends EntitySystem {

    private ImmutableArray<Entity> cameras, followEntities;

    private final static Family family = Family.all(CameraComponent.class, IsometricCameraComponent.class, PositionComponent.class).get();
    private final static Family followFamily = Family.all(PositionComponent.class, CameraFollowComponent.class).get();

    float camX = -100;
    float camZ = 100;

    float tLength = (float) Math.sqrt(camX * camX + camZ * camZ);
    float targetAngle = (float) Math.toRadians(90 - 35.264);

    float camY = (float) (Math.cos(targetAngle) * tLength);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        cameras = engine.getEntitiesFor(family);
        followEntities = engine.getEntitiesFor(followFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity camera : cameras) {
            Vector3 translation = new Vector3();
            if(followEntities.size() != 0) {
                PositionComponent followPosition = Mapper.positionComponentMapper.get(followEntities.first());
                translation.set(followPosition.position);
            }

            PositionComponent positionComponent = Mapper.positionComponentMapper.get(camera);
            IsometricCameraComponent isometricCameraComponent = Mapper.isometricCameraComponentMapper.get(camera);
            CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(camera);

            isometricCameraComponent.angle = (float) Math.floor(isometricCameraComponent.angle);
            isometricCameraComponent.target = (float) Math.floor(isometricCameraComponent.target);

            if(isometricCameraComponent.target != isometricCameraComponent.angle) {
                boolean clockWise = isometricCameraComponent.target - isometricCameraComponent.angle > 0;
                if(!clockWise) {
                    isometricCameraComponent.angle -= 5;
                }
                else {
                    isometricCameraComponent.angle += 5;
                }
            }
            else {
                isometricCameraComponent.angle %= 360;
                isometricCameraComponent.target %= 360;
                if(isometricCameraComponent.angle < 0) isometricCameraComponent.angle += 360;
                if(isometricCameraComponent.target < 0) isometricCameraComponent.target += 360;
            }

            //float theta = (float) Math.min(Math.floor(angle / isometricCameraComponent.snapAngle), Math.ceil(angle / isometricCameraComponent.snapAngle));

            float angle = isometricCameraComponent.angle  + isometricCameraComponent.offset;

            float x = (float) (Math.cos(Math.toRadians(angle)) * isometricCameraComponent.distance);
            float z = (float) (Math.sin(Math.toRadians(angle)) * isometricCameraComponent.distance);
            float tLength = (float) Math.sqrt(x * x + z * z);
            float y = (float) (Math.cos(targetAngle) * tLength);

            positionComponent.position.set(x + translation.x,y + translation.y,z + translation.z);
        }
    }
}
