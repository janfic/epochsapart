package com.janfic.games.library.ecs.systems.world;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.input.ClickableComponent;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.ModelBatchComponent;
import com.janfic.games.library.ecs.components.world.WorldComponent;
import com.janfic.games.library.ecs.components.world.WorldInputComponent;
import com.janfic.games.library.utils.voxel.CubeVoxel;
import com.janfic.games.library.utils.voxel.WorldInputListener;

public class WorldInputSystem extends EntitySystem {

    private ImmutableArray<Entity> entities, renderer;

    private static final Family worldEntity = Family.all(WorldComponent.class, WorldInputComponent.class).get();
    private static final Family rendererFamily = Family.all(CameraComponent.class, ModelBatchComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(worldEntity);
        renderer = engine.getEntitiesFor(rendererFamily);
    }

    int isDown = -1;

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(renderer.size() == 0) return;

        Entity rendererEntity = renderer.first();
        CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(rendererEntity);

        Ray ray = cameraComponent.camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());

        InputEvent event = new InputEvent();
        event.setListenerActor(new Actor());
        for (Entity entity : entities) {
            WorldComponent worldComponent = Mapper.worldComponentMapper.get(entity);
            WorldInputComponent worldInputComponent = Mapper.worldInputComponentMapper.get(entity);

            worldInputComponent.listener.setWorld(worldComponent.world);

            Vector3 selectedVoxel = worldComponent.world.getChunk(ray);

            worldInputComponent.listener.setWorldVoxel(selectedVoxel);
            if(selectedVoxel != null) {
                worldInputComponent.listener.mouseMoved(new InputEvent(), Gdx.input.getX(), Gdx.input.getY());
                if(Gdx.input.isButtonPressed(Input.Buttons.LEFT) && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    isDown = 0;
                }
                else if(isDown == 0 && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    event.setButton(0);
                    worldInputComponent.listener.clicked(event, Gdx.input.getX(), Gdx.input.getY());
                    isDown = -1;
                }
                if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && !Gdx.input.isButtonPressed(Input.Buttons.LEFT) ) {
                    isDown = 1;
                }
                else if(isDown == 1 && !Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    event.setButton(1);
                    worldInputComponent.listener.clicked(event, Gdx.input.getX(), Gdx.input.getY());
                    isDown = -1;
                }
            }
            else {
                worldInputComponent.listener.exit(new InputEvent(), Gdx.input.getX(), Gdx.input.getY(), 0, null);
            }
        }
    }
}
