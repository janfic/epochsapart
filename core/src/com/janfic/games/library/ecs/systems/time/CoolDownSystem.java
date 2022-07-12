package com.janfic.games.library.ecs.systems.time;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.time.CooldownComponent;
import com.janfic.games.library.ecs.components.time.TimeComponent;

public class CoolDownSystem extends EntitySystem {
    private ImmutableArray<Entity> entities, timeEntity;

    private static final Family timeFamily = Family.all(TimeComponent.class).get();
    private static final Family family = Family.all(CooldownComponent.class).get();

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        timeEntity = engine.getEntitiesFor(timeFamily);
        entities = engine.getEntitiesFor(family);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(timeEntity.size() == 0) return;
        TimeComponent timeComponent = Mapper.timeComponentMapper.get(timeEntity.first());

        for (Entity entity : entities) {
            CooldownComponent cooldownComponent = Mapper.cooldownComponentMapper.get(entity);
            if(cooldownComponent.currentCooldown > 0) {
                cooldownComponent.currentCooldown -= timeComponent.minutesPerRealSecond * deltaTime;
            }
            else {
                cooldownComponent.currentCooldown = 0;
            }
        }
    }
}
