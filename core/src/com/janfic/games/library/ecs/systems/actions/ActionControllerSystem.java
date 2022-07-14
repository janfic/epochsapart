package com.janfic.games.library.ecs.systems.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.actions.ActionController;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.actions.ActionControllerComponent;
import com.janfic.games.library.ecs.components.actions.ActionQueueComponent;
import com.janfic.games.library.ecs.components.actions.ActionsComponent;

import java.util.ArrayList;

/**
 * The Action Controller System is responsible for updating and passing input to entities that have an action controller.
 */
public class ActionControllerSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private static final Family entityFamily = Family.all(ActionsComponent.class, ActionQueueComponent.class, ActionControllerComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(entityFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {
            ActionsComponent actionsComponent = Mapper.actionsComponentMapper.get(entity);
            ActionControllerComponent actionControllerComponent = Mapper.actionControllerComponentMapper.get(entity);
            actionControllerComponent.actionController.evaluate(entity);
            actionControllerComponent.actionController.setActions(actionsComponent.actions);
        }
    }
}
