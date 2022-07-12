package com.janfic.games.library.actions.controllers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.janfic.games.library.actions.Action;
import com.janfic.games.library.actions.ActionController;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.actions.ActionQueueComponent;

import java.util.HashMap;
import java.util.Map;

public class PlayerActionController extends ActionController {

    Map<String, Action> keyMap;
    public PlayerActionController(Engine engine, Map<String, Action> keyMap) {
        super(engine);
        this.keyMap = keyMap;
    }

    public PlayerActionController(Engine engine) {
        super(engine);
        this.keyMap = new HashMap<>();
    }

    @Override
    public void evaluate(Entity entity) {

    }

    public Map<String, Action> getKeyMap() {
        return keyMap;
    }
}
