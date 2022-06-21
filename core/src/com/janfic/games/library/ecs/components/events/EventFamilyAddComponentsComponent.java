package com.janfic.games.library.ecs.components.events;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Family;

import java.util.List;
import java.util.function.Supplier;

public class EventFamilyAddComponentsComponent implements Component {
    public Family family;
    public List<Supplier<? extends Component>> components;
}
