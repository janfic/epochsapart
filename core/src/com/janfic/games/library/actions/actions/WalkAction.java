package com.janfic.games.library.actions.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.actions.Action;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.events.EventComponentChangeComponent;
import com.janfic.games.library.ecs.components.physics.AccelerationComponent;
import com.janfic.games.library.ecs.components.physics.PositionComponent;
import com.janfic.games.library.ecs.components.physics.VelocityComponent;

public class WalkAction extends Action {

    Vector3 location;

    /**
     * @param owner  the desired owner of this action
     * @param target the desired target of this action
     */
    public WalkAction(Entity owner, Entity target, Vector3 location) {
        super("Walk", owner, target);
        this.location = location;
    }

    @Override
    public void begin() {
        EventComponentChangeComponent<VelocityComponent> event = new EventComponentChangeComponent<>();
        event.component = Mapper.velocityComponentMapper.get(getTarget());
        event.componentConsumer = velocityComponent -> {
        };
        getTarget().add(event);
    }

    @Override
    public float act(float deltaTime) {
        this.setProgress(1);
        return this.getProgress();
    }

    @Override
    public void end() {

    }

    @Override
    public void cancel() {

    }

    private final static Family validTarget = Family.all(PositionComponent.class, VelocityComponent.class, AccelerationComponent.class).get();

    @Override
    public boolean isValidOwner(Entity entity) {
        return true;
    }

    @Override
    public boolean isValidTarget(Entity entity) {
        boolean b = validTarget.matches(entity);
        return b;
    }
}
