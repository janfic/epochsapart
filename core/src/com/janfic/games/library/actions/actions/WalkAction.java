package com.janfic.games.library.actions.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.actions.Action;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.events.EventComponentChangeComponent;
import com.janfic.games.library.ecs.components.physics.*;
import com.janfic.games.library.utils.voxel.VoxelWorld;

public class WalkAction extends Action {

    private final static Family validTarget = Family.all(PositionComponent.class, VelocityComponent.class, AccelerationComponent.class, ForceComponent.class).get();
    private static Vector3 jump = new Vector3(0, 3, 0);
    Vector3 location;
    Vector3 snap;
    VoxelWorld world;

    /**
     * @param owner  the desired owner of this action
     * @param target the desired target of this action
     */
    public WalkAction(Entity owner, Entity target, Vector3 location, VoxelWorld world) {
        super("Walk", owner, target);
        this.location = location;
        this.world = world;
        setIcon(new TextureRegion(new Texture(Gdx.files.local("move_icon.png"))));
    }

    @Override
    public void begin() {
        PositionComponent positionComponent = Mapper.positionComponentMapper.get(getTarget());
        VelocityComponent velocityComponent = Mapper.velocityComponentMapper.get(getTarget());
        Vector3 v = location.cpy().sub(positionComponent.position).nor().scl(0.1f);
        v.y = velocityComponent.velocity.y;
        velocityComponent.velocity.set(v);
        BoundingBoxComponent boundingBoxComponent = Mapper.boundingBoxComponentMapper.get(getTarget());
        snap = new Vector3();
        if (boundingBoxComponent != null) {
            snap.x = boundingBoxComponent.boundingBox.getWidth() / 2;
            snap.y = boundingBoxComponent.boundingBox.getHeight() / 2;
            snap.z = boundingBoxComponent.boundingBox.getDepth() / 2;
        }
    }

    @Override
    public float act(float deltaTime) {
        PositionComponent positionComponent = Mapper.positionComponentMapper.get(getTarget());
        VelocityComponent velocityComponent = Mapper.velocityComponentMapper.get(getTarget());
        ForceComponent forceComponent = Mapper.forceComponentMapper.get(getTarget());

        float currentDistance = positionComponent.position.dst(location);
        this.setProgress(Math.abs(currentDistance) <= 0.1f ? 1 : 0);
        Vector3 v = location.cpy().sub(positionComponent.position).nor().scl(0.1f);
        v.y = velocityComponent.velocity.y;

        Vector3 noY = v.cpy();
        noY = noY.nor().scl(1.5f);
        noY.y = 0;
        Vector3 nextPosition = positionComponent.position.cpy().add(noY);

        if (location.dst2(positionComponent.position) >= location.dst2(nextPosition)) {
            int height = world.getMaxHeight((int) (nextPosition.x), (int) (nextPosition.z));
            if (nextPosition.y < height + 1) {
                if (velocityComponent.velocity.y <= 0) {
                    forceComponent.forces.add(jump);
                }
            } else {
                forceComponent.forces.remove(jump);
            }
        }
        else {
            forceComponent.forces.remove(jump);
        }

        velocityComponent.velocity.set(v);

        return this.getProgress();
    }

    @Override
    public void end() {

        PositionComponent positionComponent = Mapper.positionComponentMapper.get(getTarget());
        VelocityComponent velocityComponent = Mapper.velocityComponentMapper.get(getTarget());
        ForceComponent forceComponent = Mapper.forceComponentMapper.get(getTarget());
        positionComponent.position.x = (float) (Math.floor(positionComponent.position.x) + snap.x);
        positionComponent.position.y = (float) (Math.floor(positionComponent.position.y) + snap.y);
        positionComponent.position.z = (float) (Math.floor(positionComponent.position.z) + snap.z);
        velocityComponent.velocity.set(Vector3.Zero);
        forceComponent.forces.remove(jump);
    }

    @Override
    public void cancel() {
        EventComponentChangeComponent<VelocityComponent> event = new EventComponentChangeComponent<>();
        PositionComponent positionComponent = Mapper.positionComponentMapper.get(getTarget());
        event.component = Mapper.velocityComponentMapper.get(getTarget());
        event.componentConsumer = velocityComponent -> {
            positionComponent.position.x = (float) (Math.floor(positionComponent.position.x) + snap.x);
            positionComponent.position.y = (float) (Math.floor(positionComponent.position.y) + snap.y);
            positionComponent.position.z = (float) (Math.floor(positionComponent.position.z) + snap.z);
            velocityComponent.velocity.set(Vector3.Zero);
        };
        getTarget().add(event);
    }

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
