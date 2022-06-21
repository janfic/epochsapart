package com.janfic.games.library.ecs.components.events;

import com.badlogic.ashley.core.Component;

import java.util.function.Consumer;

public class EventComponentChangeComponent<T extends Component> extends EventComponent {
    public Component component;
    public Consumer<T> componentConsumer;
}
