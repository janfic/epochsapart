package com.janfic.games.library.ecs.systems.physics;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.physics.RotationComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.FrameBufferComponent;
import com.janfic.games.library.ecs.components.rendering.ModelBatchComponent;
import com.janfic.games.library.ecs.components.rendering.ModelInstanceComponent;

public class ModelPositionSystem extends EntitySystem {

    private ImmutableArray<Entity> entities, rendererEntities;

    private static final Family rendererEntity = Family.all(CameraComponent.class, ModelBatchComponent.class, FrameBufferComponent.class).get();
    private static final Family entityFamily = Family.all(PositionComponent.class, ModelInstanceComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(entityFamily);
        rendererEntities = engine.getEntitiesFor(rendererEntity);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(rendererEntities.size() == 0 ) return;;
        Entity rendererEntity = rendererEntities.first();

        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(rendererEntity);

        for (Entity entity : entities) {
            PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
            RotationComponent rotationComponent = Mapper.rotationComponentMapper.get(entity);
            ModelInstanceComponent modelInstanceComponent = Mapper.modelInstanceComponentMapper.get(entity);
            modelInstanceComponent.instance.transform.setToTranslation(positionComponent.position);
            if(rotationComponent != null) {
                modelInstanceComponent.instance.transform.rotate(rotationComponent.axis, rotationComponent.angle);
            }
        }
    }
}
