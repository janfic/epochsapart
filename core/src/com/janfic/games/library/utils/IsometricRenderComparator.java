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



        return 0;
    }
}
