package com.janfic.games.library.body.bodyparts;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.janfic.games.library.body.BodyPart;

import java.util.function.Consumer;

public class Stomach extends BodyPart {

    public Stomach(float maxHealth) {
        super("Stomach", maxHealth);
        Consumer<Entity> addHunger = entity -> {
            // Add Hunger to entity
        };
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
