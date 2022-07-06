package com.janfic.games.library.ecs.components.actions;

import com.badlogic.ashley.core.Component;
import com.janfic.games.library.actions.Action;

import java.util.List;

public class ActionsComponent implements Component {
    public List<Action> actions;
}
