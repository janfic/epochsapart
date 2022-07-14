package com.janfic.games.library.actions;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Action Controller provides an interface to choose actions that are available to the entity to add to their action queue.
 * This is particularly useful for player input as well as implementing AIs.
 * <p>
 * When given input about the state of the game, an Action Controller will provide a single action that will be added to
 * the entities action queue via the Action Controller System.
 */
public abstract class ActionController {

    /**
     * The engine of the current ECS. Allows the controller to view the state of the game.
     */
    private Engine engine;

    private Set<Action> actions;

    public ActionController(Engine engine) {
        this.engine = engine;
        this.actions = new HashSet<>();
    }

    /**
     * The evaluate function takes an entity as input, and using its Action Queue Component and the engine,
     * controls the entities actions.
     * @param entity the entity in which this controller is charge of.
     */
    public abstract void evaluate(Entity entity);

    /**
     * Engine getter.
     * @return the current engine this controller is using
     */
    public Engine getEngine() {
        return engine;
    }

    public Set<Action> getActions() {
        return actions;
    }

    public void setActions(Set<Action> actions) {
        this.actions = actions;
    }
}
