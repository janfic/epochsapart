package com.janfic.games.library.actions.controllers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.janfic.games.library.actions.ActionController;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.actions.ActionQueueComponent;

public class PlayerActionController extends ActionController {
    public PlayerActionController(Engine engine) {
        super(engine);
    }

    @Override
    public void evaluate(Entity entity) {
        ActionQueueComponent actionQueueComponent = Mapper.actionQueueComponentMapper.get(entity);
        
    }
}
