package com.janfic.games.library.ecs.components.actions;

import com.badlogic.ashley.core.Component;
import com.janfic.games.library.actions.ActionController;

/**
 * The component in which the ActionControllerSystem uses to control entities actions.
 */
public class ActionControllerComponent implements Component {
    public ActionController actionController;
}
