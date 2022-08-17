package com.janfic.games.library.utils;

import com.badlogic.ashley.core.Entity;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.physics.PositionComponent;

import java.util.Comparator;

public class IsometricRenderComparator implements Comparator<Entity> {

    @Override
    public int compare(Entity a, Entity b) {
        PositionComponent posA = Mapper.positionComponentMapper.get(a);
        PositionComponent posB = Mapper.positionComponentMapper.get(b);
        if(posA.position.y != posB.position.y) {
            return (int) Math.signum(posA.position.y - posB.position.y);
        }
        return (int) Math.signum((posB.position.z + posB.position.x) - (posA.position.z + posA.position.x));
    }
}
