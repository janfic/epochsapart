package com.janfic.games.library.ecs.systems.body;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.body.BodyPart;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.body.BodyComponent;

public class BodySystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private static final Family bodyFamily = Family.all(BodyComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(bodyFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {
            BodyComponent bodyComponent = Mapper.bodyComponentMapper.get(entity);
            if(bodyComponent.owner != entity) {
                for (BodyPart part : bodyComponent.parts) {
                    part.attachToEntity(getEngine(), entity);
                }
                bodyComponent.owner = entity;
            }
            for (BodyPart part : bodyComponent.parts) {
                if(part.isAttached && part.triggerDetach(getEngine())) {
                    part.detachFromEntity(getEngine(), entity);
                }
                else if(!part.isAttached && part.triggerAttach(getEngine())) {
                    part.attachToEntity(getEngine(), entity);
                }
            }
        }
    }
}
