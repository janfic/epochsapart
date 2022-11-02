package com.janfic.games.library.ecs.systems.planet;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.planet.PlanetComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.SpriteBatchComponent;
import com.janfic.games.library.ecs.components.rendering.WorldToScreenTransformComponent;

public class PlanetUpdateSystem extends EntitySystem {
    private ImmutableArray<Entity> entities, renderer;

    private static final Family family = Family.all(PlanetComponent.class).get();
    private static final Family renderFamily = Family.all(CameraComponent.class, SpriteBatchComponent.class, WorldToScreenTransformComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(family);
        renderer = engine.getEntitiesFor(renderFamily);
    }

    @Override
    public void update(float deltaTime) {
        if(renderer.size() == 0) return;
        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(renderer.first());
        WorldToScreenTransformComponent worldToScreenTransformComponent = Mapper.worldToScreenComponentMapper.get(renderer.first());
        OrthographicCamera camera = (OrthographicCamera) cameraComponent.camera;
        for (Entity entity : entities) {
            long start = System.currentTimeMillis();
            PlanetComponent planetComponent = Mapper.planetComponentMapper.get(entity);
            planetComponent.world.updateRendering(camera, worldToScreenTransformComponent.transform);
            long end = System.currentTimeMillis();
            //System.out.println("updateTime: " + (end-start)/1000f);
        }
    }
}
