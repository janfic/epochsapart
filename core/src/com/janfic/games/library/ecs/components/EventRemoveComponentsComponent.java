package com.janfic.games.library.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.List;

public class EventRemoveComponentsComponent implements Component {
    public Entity entity;
    public List<Class<? extends Component>> componentTypes;
}
