package com.janfic.games.library.actions.actions;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.janfic.games.library.actions.Action;

public class PickUpAction extends Action {
    public PickUpAction(Entity owner, Entity target) {
        super("Pick Up", owner, target);
        setIcon(new TextureRegion(new Texture(Gdx.files.local("ui/actions/hand_icon.png"))));
    }

    @Override
    public void begin() {

    }

    @Override
    public float act(float deltaTime) {
        return 0;
    }

    @Override
    public void end() {

    }

    @Override
    public void cancel() {

    }

    @Override
    public boolean isValidOwner(Entity entity) {
        return true;
    }

    @Override
    public boolean isValidTarget(Entity entity) {
        return true;
    }
}
