package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.input.ClickableComponent;
import com.janfic.games.library.ecs.components.input.HitBoxComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.ModelBatchComponent;
import com.janfic.games.library.ecs.components.rendering.ModelInstanceComponent;
import com.janfic.games.library.ecs.components.rendering.RenderableProviderComponent;
import com.janfic.games.library.utils.ZComparator;

public class ModelClickSystem extends SortedIteratingSystem {
    private ImmutableArray<Entity> clickableEntities, rendererEntities;

    private static final Family clickableFamily = Family
            .one(ModelInstanceComponent.class)
            .all(ClickableComponent.class, HitBoxComponent.class, PositionComponent.class).get();

    private static final Family rendererFamily = Family.all(ModelBatchComponent.class, CameraComponent.class).get();

    private static ZComparator zComparator = new ZComparator();

    public ModelClickSystem() {
        super(clickableFamily, zComparator);
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        clickableEntities = engine.getEntitiesFor(clickableFamily);
        rendererEntities = engine.getEntitiesFor(rendererFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(rendererEntities.size() == 0) return;
        Entity renderer = rendererEntities.first();

        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(renderer);
        zComparator.setCameraComponent(cameraComponent);
        forceSort();
        for (Entity clickableEntity : clickableEntities) {
            processEntity(clickableEntity, deltaTime);
        }
    }

    boolean isDown = false;

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Entity renderer = rendererEntities.first();
        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(renderer);
        HitBoxComponent hitBoxComponent = Mapper.hitBoxComponentMapper.get(entity);
        ClickableComponent clickableComponent = Mapper.clickableComponentMapper.get(entity);
        PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
        ModelInstanceComponent modelInstanceComponent = Mapper.modelInstanceComponentMapper.get(entity);

        Vector3 screenPosition = cameraComponent.camera.project(positionComponent.position.cpy());
        Rectangle r = new Rectangle(hitBoxComponent.hitBox);

        float scaleX = Gdx.graphics.getWidth() / cameraComponent.camera.viewportWidth;
        float scaleY = Gdx.graphics.getHeight() / cameraComponent.camera.viewportHeight;
        r.setSize(hitBoxComponent.hitBox.getWidth() * scaleX, hitBoxComponent.hitBox.getHeight() * scaleY);
        r.setCenter(screenPosition.x, screenPosition.y);
        if(r.contains(Gdx.input.getX(), Gdx.input.getY())) {
            InputEvent event = new InputEvent();
            event.setButton(Input.Buttons.LEFT);
            clickableComponent.listener.mouseMoved(new InputEvent(), Gdx.input.getX(), Gdx.input.getY());
            clickableComponent.listener.enter(new InputEvent(), Gdx.input.getX(), Gdx.input.getY(), 1, null);
            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                isDown = true;
            }
            else if(isDown) {
                clickableComponent.listener.clicked(new InputEvent(), Gdx.input.getX(), Gdx.input.getY());
                isDown = false;
            }
        }
        else if(isDown) {
            if(!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                isDown = false;
            }
        }
        else {
            clickableComponent.listener.exit(new InputEvent(), Gdx.input.getX(), Gdx.input.getY(), 1, null);
        }
    }
}
