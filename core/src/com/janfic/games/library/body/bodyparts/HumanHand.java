package com.janfic.games.library.body.bodyparts;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.janfic.games.library.actions.Action;
import com.janfic.games.library.actions.actions.PickUpAction;
import com.janfic.games.library.body.BodyPart;
import com.janfic.games.library.ecs.Mapper;
import com.janfic.games.library.ecs.components.actions.ActionsComponent;

import java.util.function.BiConsumer;

public class HumanHand extends BodyPart {
    public HumanHand(boolean isLeft, float maxHealth) {
        super((isLeft ? "Left" : "Right" ) + " Hand", maxHealth);
        setImage(new TextureRegion(new Texture("sprites/" + (isLeft ? "left" : "right") + "_hand.png")));
        getAttachments().add((engine, entity) -> {
            ActionsComponent actionsComponent = Mapper.actionsComponentMapper.get(entity);
            actionsComponent.actions.add(new PickUpAction(entity, entity));
        });
        getDetachments().add((engine, entity) -> {
            ActionsComponent actionsComponent = Mapper.actionsComponentMapper.get(entity);
            for (Action action : actionsComponent.actions) {
                if(action.getName().equals("Pick Up")) {
                    actionsComponent.actions.remove(action);
                    break;
                }
            }
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
