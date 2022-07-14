package com.janfic.games.library.actions.controllers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.janfic.games.library.actions.Action;
import com.janfic.games.library.actions.ActionController;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.actions.ActionQueueComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerActionController extends ActionController {

    Map<String, String> map;
    Map<String, Action> keyMap;
    public PlayerActionController(Engine engine, Map<String, Action> keyMap) {
        super(engine);
        this.keyMap = keyMap;
        this.map = new HashMap<>();
    }

    public PlayerActionController(Engine engine) {
        super(engine);
        this.keyMap = new HashMap<>();
        this.map = new HashMap<>();
    }

    @Override
    public void evaluate(Entity entity) {

    }

    public void addMappingByActionName(String key, String actionName) {
        this.map.put(key, actionName);
    }

    public Map<String, Action> getKeyMap() {
        return keyMap;
    }

    @Override
    public void setActions(Set<Action> actions) {
        super.setActions(actions);
        for (String s : map.keySet()) {
            for (Action action : actions) {
                if(action.getName().equals(map.get(s))) {
                    keyMap.put(s, action);
                }
            }
        }
    }
}
