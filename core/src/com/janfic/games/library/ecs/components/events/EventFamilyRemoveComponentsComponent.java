package com.janfic.games.library.ecs.components.events;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Family;

import java.util.List;

public class EventFamilyRemoveComponentsComponent implements Component {
    public Family family;
    public List<Class<? extends Component>> components;
}
