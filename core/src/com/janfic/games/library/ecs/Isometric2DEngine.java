package com.janfic.games.library.ecs;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.components.events.EventQueueComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.SpriteBatchComponent;
import com.janfic.games.library.ecs.components.rendering.TextureRegionComponent;
import com.janfic.games.library.ecs.components.rendering.WorldToScreenTransformComponent;
import com.janfic.games.library.ecs.systems.rendering.GameRenderSystem;
import com.janfic.games.library.ecs.systems.rendering.SpriteRenderSystem;
import com.janfic.games.library.utils.ZComparator;

import java.util.LinkedList;

public class Isometric2DEngine extends Engine {

    public Isometric2DEngine() {
        Entity gameEntity = new Entity();
        EventQueueComponent eventQueueComponent = new EventQueueComponent();
        eventQueueComponent.events = new LinkedList<>();
        gameEntity.add(eventQueueComponent);

        addEntity(gameEntity);

        addSystem(new SpriteRenderSystem(new ZComparator()));
        addSystem(new SpriteRenderSystem(new ZComparator()));

        createRendererEntity();
        createSpriteEntity();
    }

    public void createSpriteEntity() {
        Entity sprite = createEntity();

        TextureRegionComponent textureRegionComponent = new TextureRegionComponent();
        textureRegionComponent.textureRegion = new TextureRegion(new Texture("move_icon.png"));

        PositionComponent positionComponent = new PositionComponent();
        positionComponent.position = new Vector3();

        sprite.add(textureRegionComponent);
        sprite.add(positionComponent);

        addEntity(sprite);
    }

    public void createRendererEntity() {
        Entity rendererEntity = createEntity();

        SpriteBatchComponent spriteBatchComponent = new SpriteBatchComponent();
        spriteBatchComponent.spriteBatch = new SpriteBatch();

        CameraComponent cameraComponent = new CameraComponent();
        OrthographicCamera orthographicCamera = new OrthographicCamera(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        cameraComponent.camera = orthographicCamera;
        orthographicCamera.position.set(0,0,0);
        orthographicCamera.update();

        WorldToScreenTransformComponent worldToScreenTransformComponent = new WorldToScreenTransformComponent();
        worldToScreenTransformComponent.transform = new WorldToScreenTransformComponent.IsometricWorldTransform();

        rendererEntity.add(spriteBatchComponent);
        rendererEntity.add(cameraComponent);
        rendererEntity.add(worldToScreenTransformComponent);

        addEntity(rendererEntity);
    }
}
