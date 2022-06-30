package com.janfic.games.library.ecs.systems.rendering;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.rendering.CameraComponent;
import com.janfic.games.library.ecs.components.rendering.SpriteBatchComponent;
import com.janfic.games.library.ecs.components.rendering.ViewportComponent;
import com.janfic.games.library.ecs.components.ui.StageComponent;

public class UserInterfaceSystem extends EntitySystem {

    private ImmutableArray<Entity> stageEntities, renderEntities;

    private static final Family renderFamily = Family.all(SpriteBatchComponent.class, CameraComponent.class, ViewportComponent.class).get();
    private static final Family stageFamily = Family.all(StageComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        stageEntities = engine.getEntitiesFor(stageFamily);
        renderEntities = engine.getEntitiesFor(renderFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity renderEntity : renderEntities) {
            SpriteBatchComponent spriteBatchComponent = Mapper.spriteBatchComponentMapper.get(renderEntity);
            CameraComponent cameraComponent = Mapper.cameraComponentMapper.get(renderEntity);
            ViewportComponent viewportComponent = Mapper.viewportComponentMapper.get(renderEntity);

            viewportComponent.viewport.setCamera(cameraComponent.camera);

            spriteBatchComponent.spriteBatch.begin();
            for (Entity stageEntity : stageEntities) {
                StageComponent stageComponent = Mapper.stageComponentMapper.get(stageEntity);
                stageComponent.stage.setViewport(viewportComponent.viewport);
                stageComponent.stage.act(deltaTime);
                stageComponent.stage.draw();
            }
            spriteBatchComponent.spriteBatch.end();
        }

    }

    @Override
    public void removedFromEngine(Engine engine) {
        super.removedFromEngine(engine);
    }
}
