package com.janfic.games.library.body.bodyparts;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.janfic.games.library.body.BodyPart;

public class HumanArm extends BodyPart {

    public HumanArm(boolean isLeft, float maxHealth) {
        super("Arm", maxHealth);
        setImage(new TextureRegion(new Texture("sprites/" + (isLeft ? "left" : "right") + "_leg.png")));
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
