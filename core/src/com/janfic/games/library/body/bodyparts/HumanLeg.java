package com.janfic.games.library.body.bodyparts;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.janfic.games.library.actions.actions.WalkAction;
import com.janfic.games.library.body.BodyPart;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.actions.ActionsComponent;
import com.janfic.games.library.utils.voxel.VoxelWorld;

import java.util.HashSet;
import java.util.function.BiConsumer;

public class HumanLeg extends BodyPart {
    private static HashSet<BiConsumer<Engine, Entity>> set;

    public HumanLeg(boolean isLeftLeg, VoxelWorld voxelWorld) {
        super((isLeftLeg ? "Left" : "Right")+ " Leg", 10);
        set = new HashSet<>(getAttachments());

        // Actions
        getAttachments().add((engine, entity) -> {
            ActionsComponent actionsComponent = Mapper.actionsComponentMapper.get(entity);
            actionsComponent.actions.add(new WalkAction(entity, entity, new Vector3(), voxelWorld));
        });
    }


    @Override
    public boolean triggerDetach(Engine engine) {
        return getCurrentHealth() <= 0;
    }

    @Override
    public boolean triggerAttach(Engine engine) {
        return getCurrentHealth() > 0;
    }
}
