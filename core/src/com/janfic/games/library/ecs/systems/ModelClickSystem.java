package com.janfic.games.library.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.input.ClickableComponent;
import com.janfic.games.library.ecs.components.input.HitBoxComponent;
import com.janfic.games.library.ecs.components.physics.BoundingBoxComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.ModelBatchComponent;
import com.janfic.games.library.ecs.components.rendering.ModelInstanceComponent;
import com.janfic.games.library.ecs.components.rendering.RenderableProviderComponent;
import com.janfic.games.library.utils.ZComparator;

public class ModelClickSystem extends SortedIteratingSystem {
    private ImmutableArray<Entity> clickableEntities, rendererEntities;

    private static final Family clickableFamily = Family.all(ClickableComponent.class, BoundingBoxComponent.class, PositionComponent.class).get();

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


    Ray pickRay;
    Entity closest;
    float dist;
    int isDown = -1;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(rendererEntities.size() == 0) return;
        Entity renderer = rendererEntities.first();
        closest = null;
        dist = Float.POSITIVE_INFINITY;

        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(renderer);
        zComparator.setCameraComponent(cameraComponent);
        //forceSort();
        for (Entity clickableEntity : clickableEntities) {
            processEntity(clickableEntity, deltaTime);
        }

        InputEvent event = new InputEvent();
        event.setListenerActor(new Actor());
        if(closest != null) {
            ClickableComponent clickableComponent = Mapper.clickableComponentMapper.get(closest);
            //System.out.println(button);
            clickableComponent.listener.enter(event, Gdx.input.getX(), Gdx.input.getY(), Input.Buttons.LEFT, null);
            clickableComponent.listener.mouseMoved(event, Gdx.input.getX(), Gdx.input.getY());

            if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                isDown = 0;
            }
            else if(isDown == 0 && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                event.setButton(0);
                clickableComponent.listener.clicked(event, Gdx.input.getX(), Gdx.input.getY());
                isDown = -1;
            }
            if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !Gdx.input.isButtonPressed(Input.Buttons.LEFT) ) {
                isDown = 1;
            }
            else if(isDown == 1 && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                event.setButton(1);
                clickableComponent.listener.clicked(event, Gdx.input.getX(), Gdx.input.getY());
                isDown = -1;
            }
        }
        else {
            isDown = -1;
        }

        for (Entity clickableEntity : clickableEntities) {
            if(clickableEntity == closest) continue;
            ClickableComponent clickableComponent = Mapper.clickableComponentMapper.get(clickableEntity);
            if(Gdx.input.getDeltaX() > 0 || Gdx.input.getDeltaY() > 0) {
                clickableComponent.listener.exit(event, Gdx.input.getX(), Gdx.input.getY(), Input.Buttons.LEFT, null);
            }
        }
    }


    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Entity renderer = rendererEntities.first();
        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(renderer);
        PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
        BoundingBoxComponent boundingBoxComponent = Mapper.boundingBoxComponentMapper.get(entity);

        pickRay = cameraComponent.camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        Vector3 position = new Vector3();

        position.set(positionComponent.position);

        float len = pickRay.direction.dot(position.x - pickRay.origin.x, position.y - pickRay.origin.y, position.z - pickRay.origin.z);

        Vector3 rayHit = new Vector3(pickRay.origin.x + pickRay.direction.x * len,
                pickRay.origin.y + pickRay.direction.y * len,
                pickRay.origin.z + pickRay.direction.z * len);

        float dist2 = position.dst2(rayHit);

        if(dist2 < dist && boundingBoxComponent.boundingBox.contains(rayHit)) {
            dist = dist2;
            closest = entity;
        }
    }
}
