package com.janfic.games.library.utils;

import com.badlogic.ashley.core.Entity;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.CameraComponent;
import com.janfic.games.library.ecs.components.PositionComponent;

import java.util.Comparator;

public class ZComparator implements Comparator<Entity> {

    private CameraComponent cameraComponent;

    public ZComparator() {
        cameraComponent = null;
    }

    public ZComparator(CameraComponent cameraComponent) {
        this.cameraComponent = cameraComponent;
    }

    public void setCameraComponent(CameraComponent cameraComponent) {
        this.cameraComponent = cameraComponent;
    }

    public CameraComponent getCameraComponent() {
        return cameraComponent;
    }

    @Override
    public int compare(Entity a, Entity b) {
        PositionComponent aPosition = Mapper.positionComponentMapper.get(a);
        PositionComponent bPosition = Mapper.positionComponentMapper.get(b);
        if(cameraComponent == null || cameraComponent.camera == null) {
            return (int)Math.signum(aPosition.position.z - bPosition.position.z);
        }
        else {
            float distA = aPosition.position.dst2(cameraComponent.camera.position);
            float distB = aPosition.position.dst2(cameraComponent.camera.position);
            return (int)Math.signum((distA - distB));
        }
    }
}
