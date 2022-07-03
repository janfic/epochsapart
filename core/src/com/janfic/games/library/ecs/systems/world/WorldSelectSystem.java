package com.janfic.games.library.ecs.systems.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.input.ClickableComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.ModelBatchComponent;
import com.janfic.games.library.ecs.components.world.WorldComponent;
import com.janfic.games.library.utils.voxel.VoxelChunk;

import java.util.Map;

public class WorldSelectSystem extends EntitySystem {

    private ImmutableArray<Entity> entities, renderer;

    private static final Family worldEntity = Family.all(WorldComponent.class, ClickableComponent.class).get();
    private static final Family rendererFamily = Family.all(CameraComponent.class, ModelBatchComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(worldEntity);
        renderer = engine.getEntitiesFor(rendererFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(renderer.size() == 0) return;

        Entity rendererEntity = renderer.first();
        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(rendererEntity);

        Ray ray = cameraComponent.camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        for (Entity entity : entities) {
            WorldComponent worldComponent = Mapper.worldComponentMapper.get(entity);
            ClickableComponent clickableComponent = Mapper.clickableComponentMapper.get(entity);

            Vector3 selectedVoxel = worldComponent.world.getChunk(ray);
            if(selectedVoxel != null) {
                worldComponent.world.set(selectedVoxel.x, selectedVoxel.y, selectedVoxel.z, (byte) 4);
            }
        }
    }
}
