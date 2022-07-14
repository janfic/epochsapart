package com.janfic.games.library.body.bodyparts;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.janfic.games.library.body.BodyPart;

public class HumanArm extends BodyPart {

    public HumanArm(float maxHealth) {
        super("Arm", maxHealth);
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
