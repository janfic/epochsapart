package com.janfic.games.library.utils;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.events.EventComponent;
import com.janfic.games.library.ecs.components.events.EventQueueComponent;

public abstract class ECSClickListener extends ClickListener {
    protected Engine engine;

    public ECSClickListener(Engine engine) {
        this.engine = engine;
    }

    public EventQueueComponent getEventQueue() {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(EventQueueComponent.class).get());
        if(entities.size() == 0 ) return null;

        Entity e = entities.first() ;
        EventQueueComponent eventQueueComponent = Mapper.eventQueueComponentMapper.get(e);
        return eventQueueComponent;
    }

    public abstract EventComponent getEvent();
}
