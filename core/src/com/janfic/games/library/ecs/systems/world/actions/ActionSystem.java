package com.janfic.games.library.ecs.systems.world.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.actions.Action;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.actions.ActionQueueComponent;
import com.janfic.games.library.ecs.components.actions.ActionsComponent;

/**
 * The Action System is responsible for maintaining entities' action queues, and applying actions within those queues.
 */
public class ActionSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private static final Family actionFamily = Family.all(ActionQueueComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(actionFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {
            ActionQueueComponent actionQueueComponent = Mapper.actionQueueComponentMapper.get(entity);
            Action currentAction = actionQueueComponent.actionQueue.peek();
            if(currentAction == null) continue;;
            if(currentAction.isComplete()) {
                currentAction.end();
                actionQueueComponent.actionQueue.poll();
                currentAction = null;
            }
            else {
                if(currentAction.isValidTarget(currentAction.getTarget()) && currentAction.isValidOwner(entity)) {
                    if(currentAction.getProgress() == -1) {
                        currentAction.begin();
                    }
                    currentAction.act(deltaTime);
                }
                else {
                    currentAction.cancel();
                    actionQueueComponent.actionQueue.poll();
                    currentAction = null;
                }
            }

            if(currentAction == null) {
                Action newAction = actionQueueComponent.actionQueue.peek();
                while(newAction != null) {
                    if(newAction.isValidOwner(entity) && newAction.isValidTarget(newAction.getTarget())) {
                        System.out.println(newAction);
                        newAction.begin();
                        break;
                    }
                    else {
                        actionQueueComponent.actionQueue.poll();
                        newAction = actionQueueComponent.actionQueue.peek();
                    }
                }
            }
        }
    }
}
