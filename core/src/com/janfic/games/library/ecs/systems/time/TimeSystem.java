package com.janfic.games.library.ecs.systems.time;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.time.TimeComponent;

import java.sql.Time;

public class TimeSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;
    private static final Family entityFamily = Family.all(TimeComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(entityFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity entity : entities) {
            TimeComponent timeComponent = Mapper.timeComponentMapper.get(entity);
            float inGameMinutes = deltaTime * timeComponent.minutesPerRealSecond;
            timeComponent.minute += inGameMinutes;
            if(timeComponent.minute >= 60) {
                timeComponent.minute -= 60;
                timeComponent.hour += 1;
            }
            if(timeComponent.hour >= 24) {
                timeComponent.hour = 0;
            }
        }
    }
}
