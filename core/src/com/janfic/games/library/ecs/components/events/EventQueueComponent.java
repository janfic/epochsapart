package com.janfic.games.library.ecs.components.events;

import com.badlogic.ashley.core.Component;

import java.util.Queue;

public class EventQueueComponent implements Component {
    public Queue<Component> events;
}
