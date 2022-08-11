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
            renderableFamily = Family.all(PositionComponent.class).one(TextureRegionComponent.class).get();

    public SpriteRenderSystem(Comparator<Entity> sorter) {
        super(renderableFamily, sorter);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        rendererEntities = engine.getEntitiesFor(rendererFamily);
        screenComponent.position = new Vector3();
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

        // Sort and Render
        batch.begin();
        super.update(deltaTime);
        batch.end();

    }

    PositionComponent screenComponent = new PositionComponent();

    @Override
    protected void processEntity(Entity entity, float v) {

        PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
        TextureRegionComponent textureRegionComponent = Mapper.textureRegionComponentMapper.get(entity);

        worldToScreenTransformComponent.transform.worldToScreen(positionComponent, screenComponent);

        batch.draw(textureRegionComponent.textureRegion, screenComponent.position.x, screenComponent.position.y);
    }
}