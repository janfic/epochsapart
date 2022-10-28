package com.janfic.games.library.ecs.systems.planet;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.*;
import com.janfic.games.library.ecs.components.planet.PlanetComponent;

public class PlanetCollisionSystem extends EntitySystem {
    private static final Family family = Family.all(PositionComponent.class, VelocityComponent.class, AccelerationComponent.class, ForceComponent.class, GravityComponent.class).get();
    private static final Family planetFamily = Family.all(PlanetComponent.class).get();
    private ImmutableArray<Entity> entities, planet;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(family);
        planet = engine.getEntitiesFor(planetFamily);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (planet.size() == 0) return;
        PlanetComponent planetComponent = Mapper.planetComponentMapper.get(planet.first());

        for (Entity entity : entities) {
            PositionComponent positionComponent = Mapper.positionComponentMapper.get(entity);
            VelocityComponent velocityComponent = Mapper.velocityComponentMapper.get(entity);
            ForceComponent forceComponent = Mapper.forceComponentMapper.get(entity);

            Vector3 currentTile = new Vector3((int) positionComponent.position.x, (int) positionComponent.position.y, (int) positionComponent.position.z);
            if (currentTile.y <= planetComponent.world.getHeight((int) currentTile.x, (int) currentTile.z)) {
                if(!forceComponent.named.containsKey("planet")) {
                    Vector3 gravity = forceComponent.named.get("gravity").cpy();
                    gravity.y *= -1;
                    Vector3 planetForce = new Vector3(gravity);
                    forceComponent.forces.add(gravity);
                    forceComponent.named.put("planet", planetForce);
                    velocityComponent.velocity.y = 0;
                }
                positionComponent.position.y = currentTile.y + 1;
            }
            else if(forceComponent.named.containsKey("planet")) {
                Vector3 force = forceComponent.named.get("planet");
                forceComponent.named.remove("planet");
                forceComponent.forces.remove(force);
            }
        }
    }
}
