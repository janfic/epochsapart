package com.janfic.games.library.ecs.components.actions;

import com.badlogic.ashley.core.Component;
import com.janfic.games.library.actions.Action;

import java.util.Queue;

/**
 *  A Queue representing an entities list of actions. Used by the ActionSystem.
 */
public class ActionQueueComponent implements Component {
   public Queue<Action> actionQueue;
}
