package com.janfic.games.library.ecs.systems.rendering;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.*;

import java.util.Comparator;

public class SpriteRenderSystem extends SortedIteratingSystem {
    private ImmutableArray<Entity> rendererEntities;

    private static final Family rendererFamily = Family.all(CameraComponent.class, SpriteBatchComponent.class).get(),
            renderableFamily = Family.all(PositionComponent.class).one(TextureRegionComponent.class).exclude(InvisibleComponent.class).get();

    public SpriteRenderSystem(Comparator<Entity> sorter) {
        super(renderableFamily, sorter);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        rendererEntities = engine.getEntitiesFor(rendererFamily);
    }

    SpriteBatch batch;
    WorldToScreenTransformComponent worldToScreenTransformComponent;

    @Override
    public void update(float deltaTime) {
        // Get Renderer ( Batch )
        if(rendererEntities.size() == 0) return;
        SpriteBatchComponent spriteBatchComponent = Mapper.spriteBatchComponentMapper.get(rendererEntities.first());
        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(rendererEntities.first());

        worldToScreenTransformComponent = Mapper.worldToScreenComponentMapper.get(rendererEntities.first());
        batch = spriteBatchComponent.spriteBatch;
        batch.setProjectionMatrix(cameraComponent.camera.combined);

        long start = System.currentTimeMillis();

        // Sort and Render
        forceSort();
        batch.begin();
        super.update(deltaTime);
        batch.end();

        long end = System.currentTimeMillis();

        System.out.println("renderFrame: " + ((end - start) / 1000f));
    }

    Vector3 screenPos = new Vector3();


    @Override
    protected void processEntity(Entity entity, float v) {
        PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
        TextureRegionComponent textureRegionComponent = Mapper.textureRegionComponentMapper.get(entity);
        OriginComponent originComponent = Mapper.originComponentMapper.get(entity);

        Vector3 s = worldToScreenTransformComponent.transform.worldToScreen(positionComponent.position, screenPos);
        if(originComponent != null && originComponent.origin != null)
            batch.draw(textureRegionComponent.textureRegion, s.x - originComponent.origin.x, s.y - originComponent.origin.y, originComponent.origin.x, originComponent.origin.y, textureRegionComponent.textureRegion.getRegionWidth(), textureRegionComponent.textureRegion.getRegionHeight(), 1f, 1f, 0f);
        else
            batch.draw(textureRegionComponent.textureRegion, s.x, s.y);
    }
}