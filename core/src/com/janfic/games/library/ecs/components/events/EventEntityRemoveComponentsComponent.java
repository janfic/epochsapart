package com.janfic.games.library.ecs.components.events;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import java.util.List;

public class EventEntityRemoveComponentsComponent extends EventComponent {
    public Entity entity;
    public List<Class<? extends Component>> componentTypes;
}
